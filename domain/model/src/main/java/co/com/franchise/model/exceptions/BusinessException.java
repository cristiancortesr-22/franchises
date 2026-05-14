package co.com.franchise.model.exceptions;

import co.com.franchise.model.enums.ErrorMessage;
import lombok.Getter;

@Getter
public class BusinessException extends AppException {

    public BusinessException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    public BusinessException(String message, ErrorMessage errorMessage) {
        super(message, errorMessage);
    }
}
