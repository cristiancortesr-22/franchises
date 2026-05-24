package co.com.franchise.api.franchise;

import co.com.franchise.api.franchise.dto.FranchiseRequest;
import co.com.franchise.api.mapper.HandlerMapper;
import co.com.franchise.api.BaseHandler;
import co.com.franchise.api.validator.FranchiseValidate;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.AppException;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
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
@Slf4j
public class FranchiseHandler extends BaseHandler {

    private final FranchiseUseCase franchiseUseCase;
    private final CircuitBreaker circuitBreaker;

    public FranchiseHandler(FranchiseUseCase franchiseUseCase, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.franchiseUseCase = franchiseUseCase;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("rateReference");
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseRequest -> {
                    Errors errors = new BeanPropertyBindingResult(franchiseRequest, FranchiseRequest.class.getName());

                    Validator validator = new FranchiseValidate();
                    validator.validate(franchiseRequest, errors);

                    if (errors.getErrorCount() > 0) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT);
                    } else {
                        return franchiseUseCase.create(HandlerMapper.MAPPER.toFranchiseParam(franchiseRequest))
                                .flatMap(franchise -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toFranchiseResponse(franchise)))
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .onErrorResume(CallNotPermittedException.class, exception -> {
                                    log.warn("Circuit breaker is OPEN for franchise create");
                                    return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.SERVICE_UNAVAILABLE);
                                })
                                .onErrorResume(AppException.class, error -> buildErrorResponse(HttpStatus.BAD_REQUEST,
                                        error.getErrorMessage()))
                                .onErrorResume(throwable -> {
                                    log.error("Error in franchise create", throwable);
                                    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_ERROR);
                                });
                    }
                });
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Long franchiseId = Long.valueOf(serverRequest.pathVariable("id"));

        return serverRequest.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseRequest -> {
                    Errors errors = new BeanPropertyBindingResult(franchiseRequest, FranchiseRequest.class.getName());

                    Validator validator = new FranchiseValidate();
                    validator.validate(franchiseRequest, errors);

                    if (errors.getErrorCount() > 0) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT);
                    } else {
                        return franchiseUseCase.updateName(HandlerMapper.MAPPER.toFranchiseParam(franchiseRequest), franchiseId)
                                .flatMap(franchise -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toFranchiseResponse(franchise)))
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .onErrorResume(CallNotPermittedException.class, exception -> {
                                    log.warn("Circuit breaker is OPEN for franchise update");
                                    return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.SERVICE_UNAVAILABLE);
                                })
                                .onErrorResume(AppException.class, error -> buildErrorResponse(HttpStatus.BAD_REQUEST,
                                        error.getErrorMessage()))
                                .onErrorResume(throwable -> {
                                    log.error("Error in franchise update", throwable);
                                    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_ERROR);
                                });
                    }
                });
    }
}
