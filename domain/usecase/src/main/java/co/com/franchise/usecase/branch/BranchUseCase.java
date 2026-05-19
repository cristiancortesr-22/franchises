package co.com.franchise.usecase.branch;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import co.com.franchise.model.branch.gateways.BranchRepository;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {

    private final BranchRepository branchRepository;
    private final FranchiseUseCase franchiseUseCase;

    public Mono<Branch> create(BranchParam branchParam) {
        return franchiseUseCase.getById(branchParam.getFranchiseId())
                .flatMap(franchise -> branchRepository.save(branchParam))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.FRANCHISE_DOES_NOT_EXIST)));
    }

    public Mono<Branch> get(Long id) {
        return branchRepository.get(id);
    }

    public Mono<Branch> updateName(Long id, String name) {
        return get(id)
                .flatMap(branch -> branchRepository.update(branch.toBuilder().name(name).build()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.BRANCH_DOES_NOT_EXIST)));
    }
}
