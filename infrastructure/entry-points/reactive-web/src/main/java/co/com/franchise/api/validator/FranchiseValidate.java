package co.com.franchise.api.validator;

import co.com.franchise.api.franchise.dto.FranchiseRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

public class FranchiseValidate implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FranchiseRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FranchiseRequest franchiseRequest = (FranchiseRequest) target;

        if (Objects.isNull(franchiseRequest.getName()) || franchiseRequest.getName().isEmpty()) {
            errors.rejectValue("name", "Campo obligatorio");
        }
    }
}
