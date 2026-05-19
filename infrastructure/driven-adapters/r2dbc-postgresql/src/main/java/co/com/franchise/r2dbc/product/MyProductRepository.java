package co.com.franchise.r2dbc.product;

import co.com.franchise.r2dbc.model.ProductEntity;
import co.com.franchise.r2dbc.model.dto.ProductViewDTO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    @Modifying
    @Query("UPDATE public.product SET status= :status WHERE id= :id")
    Mono<Boolean> updateStatus(String status, Long id);

    @Modifying
    @Query("UPDATE public.product SET stock= :stock WHERE id= :id")
    Mono<Boolean> updateStock(int stock, Long id);

    @Modifying
    @Query("UPDATE public.product SET name= :name WHERE id= :id")
    Mono<Boolean> updateName(String name, Long id);

    @Modifying
    @Query("UPDATE public.product SET stock= :stock, status= :status WHERE id= :id")
    Mono<Boolean> updateStockAndStatus(int stock, String status, Long id);

    @Query("""
               WITH RankedProducts AS (
                   SELECT
                       f.id AS franchise_id,
                       f.name AS franchise_name,
                       b.id AS branch_id,
                       b.name AS branch_name,
                       p.name AS product_name,
                       SUM(p.stock) AS total_stock,
                       RANK() OVER (PARTITION BY b.id ORDER BY SUM(p.stock) DESC) AS rank
                   FROM product p
                   JOIN branch b ON p.branch_id = b.id
                   JOIN franchise f ON b.franchise_id = f.id
                   WHERE f.id = :franchise_id and p.status = :status
                   GROUP BY f.id, f.name, b.id, b.name, p.name
               )
               SELECT *
               FROM RankedProducts
               WHERE rank = 1
               ORDER BY branch_id;
            """)
    Flux<ProductViewDTO> findTopStockProductsByFranchise(Long franchiseId, String status);

    Mono<ProductEntity> findByIdAndStatus(Long id, String status);
    Mono<ProductEntity> findByNameAndBranchIdAndStatus(String name, Long branchId, String status);
}
