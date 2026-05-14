package co.com.franchise.api.product.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProductUpdateStockRequest {
    int stock;
}
