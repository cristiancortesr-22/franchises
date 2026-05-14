package co.com.franchise.r2dbc.product;

import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.exceptions.InfrastructureException;
import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import co.com.franchise.model.product.gateways.ProductRepository;
import co.com.franchise.r2dbc.mapper.ProductMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductRepository {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DELETE = "DELETE";
    private final MyProductRepository myProductRepository;
    private final CircuitBreaker databaseCircuitBreaker;

    @Override
    public Mono<Product> save(ProductParam productParam) {
        return myProductRepository.save(ProductMapper.INSTANCE.toProductEntity(productParam))
                .map(ProductMapper.INSTANCE::toProduct)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Save product request", kv("saveProductRequest", productParam)))
                .doOnSuccess(product -> log.info("Saved product response", kv("savedProductResponse", product)))
                .onErrorResume(DuplicateKeyException.class, error -> recoverDeletedRecord(productParam))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.PRODUCT_CREATION_FAILED))
                .doOnError(throwable -> log.error("Save product error", throwable));
    }

    private Mono<Product> recoverDeletedRecord(ProductParam productParam) {
        return myProductRepository.findByNameAndBranchIdAndStatus(productParam.getName(),
                        productParam.getBranchId(), STATUS_DELETE)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.PRODUCT_ALREADY_EXISTS)))
                .flatMap(productEntity ->
                        myProductRepository.updateStockAndStatus(productParam.getStock(), STATUS_ACTIVE, productEntity.getId())
                                .filter(updated -> updated.equals(Boolean.TRUE))
                                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.PRODUCT_UPDATE_STOCK_AND_STATUS_FAILED)))
                                .flatMap(updated -> myProductRepository.findById(productEntity.getId())
                                        .flatMap(productEntityUpdate ->
                                                Mono.just(ProductMapper.INSTANCE.toProduct(productEntityUpdate)))
                                        .doOnSuccess(product -> log.info("Product Recovery response", kv("productRecoveryResponse", product))))
                );
    }
}
