package co.com.franchise.r2dbc.franchise;

import co.com.franchise.r2dbc.model.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyFranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
}
