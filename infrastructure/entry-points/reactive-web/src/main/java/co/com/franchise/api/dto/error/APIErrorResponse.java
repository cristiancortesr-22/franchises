package co.com.franchise.api.dto.error;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class APIErrorResponse {
    Integer status;
    String code;
    String message;
}
