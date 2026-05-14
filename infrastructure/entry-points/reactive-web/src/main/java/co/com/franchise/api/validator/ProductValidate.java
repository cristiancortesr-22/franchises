package co.com.franchise.api.validator;

import co.com.franchise.api.product.dto.ProductRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

public class ProductValidate implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductRequest productRequest = (ProductRequest) target;

        if (Objects.isNull(productRequest.getName()) || productRequest.getName().isEmpty()) {
            errors.rejectValue("name", "Campo obligatorio");
        }

        if (Objects.isNull(productRequest.getBranchId()) || productRequest.getBranchId() < 0) {
            errors.rejectValue("BranchId", "Campo obligatorio y debe ser mayor que 0");
        }
    }
}
