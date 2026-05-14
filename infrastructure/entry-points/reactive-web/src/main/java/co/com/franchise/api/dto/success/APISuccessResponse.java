package co.com.franchise.api.dto.success;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class APISuccessResponse {

    Metadata metadata;
    Object data;

    @Value
    @Builder
    @Jacksonized
    public static class Metadata {
        LocalDateTime responseDataTime;
    }
}
