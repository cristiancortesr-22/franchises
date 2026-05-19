package co.com.franchise.api.validator;

import co.com.franchise.api.branch.dto.BranchUpdateRequest;
import co.com.franchise.api.product.dto.ProductUpdateStockRequest;
import lombok.experimental.UtilityClass;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import reactor.core.publisher.Mono;

@UtilityClass
public class UtilValidate {

    public static Mono<Boolean> validateRequestUpdateStock(ProductUpdateStockRequest productUpdateStockRequest, Long id) {
        boolean hasErrors = productUpdateStockRequest == null || id == null || id <= 0;
        return hasErrors ? Mono.just(true) : Mono.empty();
    }

    public static Mono<Boolean> validateRequestById(Long id) {
        boolean hasErrors = id == null || id <= 0;
        return hasErrors ? Mono.just(true) : Mono.empty();
    }

    public static Mono<Errors> validateBranchUpdateRequest(BranchUpdateRequest branchUpdateRequest, Long id) {
        Errors errors = new BeanPropertyBindingResult(branchUpdateRequest, BranchUpdateRequest.class.getName());

        if (branchUpdateRequest.getName() == null || branchUpdateRequest.getName().isEmpty()) {
            errors.rejectValue("name", "Campo obligatorio");
        }

        boolean hasIdErrors = id == null || id <= 0;
        if (hasIdErrors) {
            errors.rejectValue("name", "Id invalido");
        }

        if (hasIdErrors || errors.getErrorCount() > 0) {
            return Mono.just(errors);
        }

        return Mono.empty();
    }
}
