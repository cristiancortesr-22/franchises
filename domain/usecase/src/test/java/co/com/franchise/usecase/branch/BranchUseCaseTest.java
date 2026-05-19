package co.com.franchise.usecase.branch;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import co.com.franchise.model.branch.gateways.BranchRepository;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseUseCase franchiseUseCase;

    @InjectMocks
    private BranchUseCase branchUseCase;

    private Branch branch;
    private BranchParam branchParam;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder().id(1L).name("Franchise").build();
        branch = Branch.builder().id(1L).name("Branch A").franchiseId(1L).build();
        branchParam = BranchParam.builder().name("Branch A").franchiseId(1L).build();
    }

    @Test
    void create_shouldReturnBranch_whenFranchiseExists() {
        when(franchiseUseCase.getById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.save(branchParam)).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.create(branchParam))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void create_shouldThrowBusinessException_whenFranchiseNotExists() {
        when(franchiseUseCase.getById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.create(branchParam))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.FRANCHISE_DOES_NOT_EXIST)
                .verify();
    }

    @Test
    void get_shouldReturnBranch_whenExists() {
        when(branchRepository.get(1L)).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.get(1L))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void get_shouldReturnEmpty_whenNotExists() {
        when(branchRepository.get(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.get(99L))
                .verifyComplete();
    }

    @Test
    void updateName_shouldReturnUpdatedBranch_whenBranchExists() {
        Branch updatedBranch = branch.toBuilder().name("New Branch Name").build();
        when(branchRepository.get(1L)).thenReturn(Mono.just(branch));
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(branchUseCase.updateName(1L, "New Branch Name"))
                .expectNext(updatedBranch)
                .verifyComplete();
    }

    @Test
    void updateName_shouldThrowBusinessException_whenBranchNotExists() {
        when(branchRepository.get(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateName(99L, "New Name"))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.BRANCH_DOES_NOT_EXIST)
                .verify();
    }
}
