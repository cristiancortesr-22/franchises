package co.com.franchise.api.validator;

import co.com.franchise.api.branch.dto.BranchRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

public class BranchValidate implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BranchRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BranchRequest branchRequest = (BranchRequest) target;

        if (Objects.isNull(branchRequest.getName()) || branchRequest.getName().isEmpty()) {
            errors.rejectValue("name", "Campo obligatorio");
        }

        if (Objects.isNull(branchRequest.getFranchiseId()) || branchRequest.getFranchiseId() < 0) {
            errors.rejectValue("FranchiseId", "Campo obligatorio y debe ser mayor que 0");
        }
    }
}
