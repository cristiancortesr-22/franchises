package co.com.franchise.api.product;

import co.com.franchise.api.mapper.HandlerMapper;
import co.com.franchise.api.product.dto.ProductRequest;
import co.com.franchise.api.BaseHandler;
import co.com.franchise.api.validator.ProductValidate;
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
}
