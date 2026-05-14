package co.com.franchise.r2dbc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductViewDTO {
    private Long franchiseId;
    private String franchiseName;
    private Long branchId;
    private String branchName;
    private String productName;
    private int totalStock;
}
