package co.com.franchise.api.branch;

import co.com.franchise.api.branch.dto.BranchRequest;
import co.com.franchise.api.branch.dto.BranchUpdateRequest;
import co.com.franchise.api.mapper.HandlerMapper;
import co.com.franchise.api.BaseHandler;
import co.com.franchise.api.validator.BranchValidate;
import co.com.franchise.api.validator.UtilValidate;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.AppException;
import co.com.franchise.usecase.branch.BranchUseCase;
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
public class BranchHandler extends BaseHandler {

    private final BranchUseCase branchUseCase;
    private final CircuitBreaker circuitBreaker;

    public BranchHandler(BranchUseCase branchUseCase, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.branchUseCase = branchUseCase;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("rateReference");
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequest.class)
                .flatMap(branchRequest -> {
                    Errors errors = new BeanPropertyBindingResult(branchRequest, BranchRequest.class.getName());

                    Validator validator = new BranchValidate();
                    validator.validate(branchRequest, errors);

                    if (errors.getErrorCount() > 0) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT);
                    } else {
                        return branchUseCase.create(HandlerMapper.MAPPER.toBranchParam(branchRequest))
                                .flatMap(branch -> buildSuccessResponse(HttpStatus.OK,
                                        HandlerMapper.MAPPER.toBranchResponse(branch)))
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .onErrorResume(CallNotPermittedException.class, exception -> {
                                    log.warn("Circuit breaker is OPEN for branch create");
                                    return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.SERVICE_UNAVAILABLE);
                                })
                                .onErrorResume(AppException.class, error ->
                                        buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                                .onErrorResume(throwable -> {
                                    log.error("Error in branch create", throwable);
                                    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_ERROR);
                                });
                    }
                });
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Long branchId = Long.valueOf(serverRequest.pathVariable("id"));

        return serverRequest.bodyToMono(BranchUpdateRequest.class)
                .flatMap(branchUpdateRequest ->
                        UtilValidate.validateBranchUpdateRequest(branchUpdateRequest, branchId)
                                .flatMap(errors -> buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INPUT))
                                .switchIfEmpty(Mono.defer(() -> branchUseCase.updateName(branchId, branchUpdateRequest.getName())
                                        .flatMap(branch -> buildSuccessResponse(HttpStatus.OK, HandlerMapper.MAPPER.toBranchResponse(branch)))
                                        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                        .onErrorResume(CallNotPermittedException.class, exception -> {
                                            log.warn("Circuit breaker is OPEN for branch update");
                                            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.SERVICE_UNAVAILABLE);
                                        })
                                        .onErrorResume(AppException.class, error ->
                                                buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                                        .onErrorResume(throwable -> {
                                            log.error("Error in branch update", throwable);
                                            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_ERROR);
                                        }))));
    }
}
