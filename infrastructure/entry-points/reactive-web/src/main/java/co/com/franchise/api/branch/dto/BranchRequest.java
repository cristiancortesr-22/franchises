package co.com.franchise.api.branch.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class BranchRequest {
    String name;
    Long franchiseId;
}
