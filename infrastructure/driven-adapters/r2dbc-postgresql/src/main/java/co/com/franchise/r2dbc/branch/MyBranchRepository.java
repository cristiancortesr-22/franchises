package co.com.franchise.r2dbc.branch;

import co.com.franchise.r2dbc.model.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyBranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
}
