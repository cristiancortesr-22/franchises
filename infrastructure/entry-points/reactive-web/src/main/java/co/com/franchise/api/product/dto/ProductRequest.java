package co.com.franchise.api.product.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProductRequest {
    String name;
    int stock;
    Long branchId;
}
