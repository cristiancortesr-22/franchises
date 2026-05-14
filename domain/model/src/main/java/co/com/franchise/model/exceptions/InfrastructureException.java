package co.com.franchise.model.exceptions;

import co.com.franchise.model.enums.ErrorMessage;
import lombok.Getter;

@Getter
public class InfrastructureException extends AppException {

    public InfrastructureException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    public InfrastructureException(Throwable cause, ErrorMessage errorMessage) {
        super(cause, errorMessage);
    }
}
