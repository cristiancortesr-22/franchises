package co.com.franchise.r2dbc.franchise;

import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.exceptions.InfrastructureException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import co.com.franchise.model.franchise.gateways.FranchiseRepository;
import co.com.franchise.r2dbc.mapper.FranchiseMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseAdapter implements FranchiseRepository {

    private final MyFranchiseRepository myFranchiseRepository;
    private final CircuitBreaker databaseCircuitBreaker;

    @Override
    public Mono<Franchise> save(FranchiseParam franchiseParam) {
        return myFranchiseRepository
                .save(FranchiseMapper.INSTANCE.toFranchiseEntity(franchiseParam))
                .map(FranchiseMapper.INSTANCE::toFranchise)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Save franchise request", kv("saveFranchiseRequest", franchiseParam)))
                .doOnSuccess(franchise -> log.info("Saved franchise response", kv("savedFranchiseResponse", franchise)))
                .doOnError(throwable -> log.error("Save franchise error", throwable))
                .onErrorMap(DuplicateKeyException.class, error ->
                        new BusinessException(ErrorMessage.FRANCHISE_ALREADY_EXISTS))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.FRANCHISE_CREATION_FAILED));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return myFranchiseRepository.findById(id)
                .map(FranchiseMapper.INSTANCE::toFranchise)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Get by Id franchise request", kv("getFranchiseRequest", Map.of("id", id))))
                .doOnSuccess(franchise -> log.info("Get by Id franchise response", kv("getFranchiseResponse", franchise)))
                .doOnError(throwable -> log.error("Get by Id franchise error", throwable))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.FRANCHISE_GET_FAILED));
    }
}
