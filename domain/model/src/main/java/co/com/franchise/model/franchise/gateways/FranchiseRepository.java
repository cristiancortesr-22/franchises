package co.com.franchise.model.franchise.gateways;

import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(FranchiseParam franchiseParam);
    Mono<Franchise> findById(Long id);
    Mono<Franchise> update(Franchise franchise);
}
