package co.com.franchise.model.branch.gateways;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(BranchParam branchParam);
    Mono<Branch> get(Long id);
    Mono<Branch> update(Branch branch);
}
