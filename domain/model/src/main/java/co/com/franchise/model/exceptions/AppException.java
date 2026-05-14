package co.com.franchise.model.exceptions;

import co.com.franchise.model.enums.ErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public AppException(String message, ErrorMessage errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }

    public AppException(Throwable cause, ErrorMessage errorMessage) {
        super(cause);
        this.errorMessage = errorMessage;
    }
}
