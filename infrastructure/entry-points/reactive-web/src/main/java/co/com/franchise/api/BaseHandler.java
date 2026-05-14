package co.com.franchise.api;

import co.com.franchise.api.dto.error.APIErrorResponse;
import co.com.franchise.api.dto.success.APISuccessResponse;
import co.com.franchise.model.enums.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public abstract class BaseHandler {

    public Mono<ServerResponse> buildSuccessResponse(HttpStatus status, Object response) {
        APISuccessResponse apiSuccessResponse = APISuccessResponse.builder()
                .metadata(buildSuccessMetadata())
                .data(response)
                .build();

        return ServerResponse.status(status).bodyValue(apiSuccessResponse);
    }

    public Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, ErrorMessage errorMessage) {
        APIErrorResponse errorResponse = APIErrorResponse.builder()
                .status(httpStatus.value())
                .code(errorMessage.getCode())
                .message(errorMessage.getMessage())
                .build();

        return ServerResponse.status(httpStatus).bodyValue(errorResponse);
    }

    private APISuccessResponse.Metadata buildSuccessMetadata() {
        return APISuccessResponse.Metadata.builder()
                .responseDataTime(LocalDateTime.now())
                .build();
    }
}
