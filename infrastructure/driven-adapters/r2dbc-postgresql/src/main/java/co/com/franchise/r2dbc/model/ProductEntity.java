package co.com.franchise.r2dbc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("product")
public class ProductEntity {

    @Id
    private Long id;
    private String name;
    private int stock;

    @Column("branch_id")
    private Long branchId;
    private String status;
}
