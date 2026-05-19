package co.com.franchise.api.product;

import co.com.franchise.api.mapper.HandlerMapper;
import co.com.franchise.api.product.dto.ProductRequest;
import co.com.franchise.api.product.dto.ProductUpdateNameRequest;
import co.com.franchise.api.product.dto.ProductUpdateStockRequest;
import co.com.franchise.api.BaseHandler;
import co.com.franchise.api.validator.ProductValidate;
import co.com.franchise.api.validator.UtilValidate;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.AppException;
import co.com.franchise.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductHandler extends BaseHandler {

    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ProductRequest.class)
                .flatMap(productRequest -> {
                    Errors errors = new BeanPropertyBindingResult(productRequest, ProductRequest.class.getName());

                    Validator validator = new ProductValidate();
                    validator.validate(productRequest, errors);

                    if (errors.getErrorCount() > 0) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT);
                    } else {
                        return productUseCase.create(HandlerMapper.MAPPER.toProductParam(productRequest))
                                .flatMap(product -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toProductResponse(product)))
                                .onErrorResume(AppException.class, error ->
                                        buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                                .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        ErrorMessage.INTERNAL_ERROR));
                    }
                });
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        Long productId = Long.valueOf(serverRequest.pathVariable("id"));

        return UtilValidate.validateRequestById(productId)
                .flatMap(error -> buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT))
                .switchIfEmpty(Mono.defer(() -> productUseCase.delete(productId)
                        .flatMap(product -> buildSuccessResponse(HttpStatus.OK, Map.of("message", "Product deleted successfully!")))
                        .onErrorResume(AppException.class, error ->
                                buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                        .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorMessage.INTERNAL_ERROR))));
    }

    public Mono<ServerResponse> updateStock(ServerRequest serverRequest) {
        Long productId = Long.valueOf(serverRequest.pathVariable("id"));

        return serverRequest.bodyToMono(ProductUpdateStockRequest.class)
                .flatMap(productUpdateStockRequest ->
                        UtilValidate.validateRequestUpdateStock(productUpdateStockRequest, productId)
                                .flatMap(error -> buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT))
                                .switchIfEmpty(Mono.defer(() -> productUseCase.updateStock(productId, productUpdateStockRequest.getStock())
                                        .flatMap(product -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toProductResponse(product)))
                                        .onErrorResume(AppException.class, error ->
                                                buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                                        .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                                ErrorMessage.INTERNAL_ERROR)))));
    }

    public Mono<ServerResponse> getTopProducts(ServerRequest serverRequest) {
        Long franchiseId = Long.valueOf(serverRequest.pathVariable("franchiseId"));

        return UtilValidate.validateRequestById(franchiseId)
                .flatMap(error -> buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT))
                .switchIfEmpty(Mono.defer(() -> productUseCase.getTopStockProductsByFranchise(franchiseId)
                        .flatMap(productViews -> buildSuccessResponse(HttpStatus.OK,
                                HandlerMapper.MAPPER.toProductViewResponse(productViews)))
                        .onErrorResume(AppException.class, error ->
                                buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                        .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorMessage.INTERNAL_ERROR))));
    }

    public Mono<ServerResponse> updateName(ServerRequest serverRequest) {
        Long productId = Long.valueOf(serverRequest.pathVariable("id"));

        return serverRequest.bodyToMono(ProductUpdateNameRequest.class)
                .flatMap(productUpdateNameRequest -> {
                    boolean hasErrors = productUpdateNameRequest == null || productId == null || productId <= 0
                            || productUpdateNameRequest.getName() == null || productUpdateNameRequest.getName().isBlank();
                    if (hasErrors) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT);
                    }
                    return productUseCase.updateName(productId, productUpdateNameRequest.getName())
                            .flatMap(product -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toProductResponse(product)))
                            .onErrorResume(AppException.class, error ->
                                    buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                            .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessage.INTERNAL_ERROR));
                });
    }
}
