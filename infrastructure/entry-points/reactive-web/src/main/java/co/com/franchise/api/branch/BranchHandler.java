package co.com.franchise.api.branch;

import co.com.franchise.api.branch.dto.BranchRequest;
import co.com.franchise.api.mapper.HandlerMapper;
import co.com.franchise.api.BaseHandler;
import co.com.franchise.api.validator.BranchValidate;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.AppException;
import co.com.franchise.usecase.branch.BranchUseCase;
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
public class BranchHandler extends BaseHandler {

    private final BranchUseCase branchUseCase;

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
                                .onErrorResume(AppException.class, error ->
                                        buildErrorResponse(HttpStatus.BAD_REQUEST, error.getErrorMessage()))
                                .onErrorResume(throwable -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        ErrorMessage.INTERNAL_ERROR));
                    }
                });
    }
}
