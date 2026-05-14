package co.com.franchise.api.franchise.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class FranchiseRequest {
    String name;
}
