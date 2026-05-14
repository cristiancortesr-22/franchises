package co.com.franchise.api.validator;

import co.com.franchise.api.product.dto.ProductUpdateStockRequest;
import lombok.experimental.UtilityClass;
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
}
