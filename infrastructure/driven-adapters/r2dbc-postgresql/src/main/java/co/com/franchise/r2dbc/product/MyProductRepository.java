package co.com.franchise.r2dbc.product;

import co.com.franchise.r2dbc.model.ProductEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MyProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    @Modifying
    @Query("UPDATE public.product SET stock= :stock, status= :status WHERE id= :id")
    Mono<Boolean> updateStockAndStatus(int stock, String status, Long id);

    Mono<ProductEntity> findByNameAndBranchIdAndStatus(String name, Long branchId, String status);
}
