package co.com.franchise.usecase.franchise;

import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import co.com.franchise.model.franchise.gateways.FranchiseRepository;
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
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    private Franchise franchise;
    private FranchiseParam franchiseParam;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder().id(1L).name("Test Franchise").build();
        franchiseParam = FranchiseParam.builder().name("Test Franchise").build();
    }

    @Test
    void create_shouldReturnFranchise_whenSaveSucceeds() {
        when(franchiseRepository.save(franchiseParam)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.create(franchiseParam))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void getById_shouldReturnFranchise_whenExists() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.getById(1L))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void getById_shouldReturnEmpty_whenNotExists() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getById(99L))
                .verifyComplete();
    }

    @Test
    void updateName_shouldReturnUpdatedFranchise_whenFranchiseExists() {
        Franchise updatedFranchise = franchise.toBuilder().name("New Name").build();
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        FranchiseParam updateParam = FranchiseParam.builder().name("New Name").build();

        StepVerifier.create(franchiseUseCase.updateName(updateParam, 1L))
                .expectNext(updatedFranchise)
                .verifyComplete();
    }

    @Test
    void updateName_shouldThrowBusinessException_whenFranchiseNotExists() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        FranchiseParam updateParam = FranchiseParam.builder().name("New Name").build();

        StepVerifier.create(franchiseUseCase.updateName(updateParam, 99L))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.FRANCHISE_DOES_NOT_EXIST)
                .verify();
    }
}
