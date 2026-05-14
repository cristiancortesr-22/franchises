package co.com.franchise.usecase.franchise;

import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import co.com.franchise.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(FranchiseParam franchiseParam) {
        return franchiseRepository.save(franchiseParam);
    }

    public Mono<Franchise> getById(Long id) {
        return franchiseRepository.findById(id);
    }
}
