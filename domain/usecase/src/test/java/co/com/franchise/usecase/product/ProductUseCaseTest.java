package co.com.franchise.usecase.product;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import co.com.franchise.model.product.ProductView;
import co.com.franchise.model.product.gateways.ProductRepository;
import co.com.franchise.usecase.branch.BranchUseCase;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchUseCase branchUseCase;

    @Mock
    private FranchiseUseCase franchiseUseCase;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Product product;
    private ProductParam productParam;
    private Branch branch;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        product = Product.builder().id(1L).name("Product A").stock(100).branchId(1L).build();
        productParam = ProductParam.builder().name("Product A").stock(100).branchId(1L).build();
        branch = Branch.builder().id(1L).name("Branch").franchiseId(1L).build();
        franchise = Franchise.builder().id(1L).name("Franchise").build();
    }

    @Test
    void create_shouldReturnProduct_whenBranchExists() {
        when(branchUseCase.get(1L)).thenReturn(Mono.just(branch));
        when(productRepository.save(productParam)).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.create(productParam))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void create_shouldThrowBusinessException_whenBranchNotExists() {
        when(branchUseCase.get(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.create(productParam))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.BRANCH_DOES_NOT_EXIST)
                .verify();
    }

    @Test
    void delete_shouldReturnTrue_whenProductExistsAndDeleteSucceeds() {
        when(productRepository.getById(1L)).thenReturn(Mono.just(product));
        when(productRepository.delete(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.delete(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void delete_shouldThrowBusinessException_whenProductNotExists() {
        when(productRepository.getById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete(99L))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_DOES_NOT_EXIST)
                .verify();
    }

    @Test
    void delete_shouldThrowBusinessException_whenDeleteFails() {
        when(productRepository.getById(1L)).thenReturn(Mono.just(product));
        when(productRepository.delete(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(productUseCase.delete(1L))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_DELETE_FAILED)
                .verify();
    }

    @Test
    void updateStock_shouldReturnUpdatedProduct_whenProductExists() {
        Product updatedProduct = product.toBuilder().stock(200).build();
        when(productRepository.getById(1L)).thenReturn(Mono.just(product)).thenReturn(Mono.just(updatedProduct));
        when(productRepository.updateStock(1L, 200)).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.updateStock(1L, 200))
                .expectNext(updatedProduct)
                .verifyComplete();
    }

    @Test
    void updateStock_shouldThrowBusinessException_whenProductNotExists() {
        when(productRepository.getById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(99L, 200))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_DOES_NOT_EXIST)
                .verify();
    }

    @Test
    void updateStock_shouldThrowBusinessException_whenUpdateFails() {
        when(productRepository.getById(1L)).thenReturn(Mono.just(product));
        when(productRepository.updateStock(1L, 200)).thenReturn(Mono.just(false));

        StepVerifier.create(productUseCase.updateStock(1L, 200))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_UPDATE_STOCK_FAILED)
                .verify();
    }

    @Test
    void updateName_shouldReturnUpdatedProduct_whenProductExists() {
        Product updatedProduct = product.toBuilder().name("New Name").build();
        when(productRepository.getById(1L)).thenReturn(Mono.just(product)).thenReturn(Mono.just(updatedProduct));
        when(productRepository.updateName(1L, "New Name")).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.updateName(1L, "New Name"))
                .expectNext(updatedProduct)
                .verifyComplete();
    }

    @Test
    void updateName_shouldThrowBusinessException_whenProductNotExists() {
        when(productRepository.getById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateName(99L, "New Name"))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_DOES_NOT_EXIST)
                .verify();
    }

    @Test
    void updateName_shouldThrowBusinessException_whenUpdateFails() {
        when(productRepository.getById(1L)).thenReturn(Mono.just(product));
        when(productRepository.updateName(1L, "New Name")).thenReturn(Mono.just(false));

        StepVerifier.create(productUseCase.updateName(1L, "New Name"))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.PRODUCT_UPDATE_NAME_FAILED)
                .verify();
    }

    @Test
    void getTopStockProductsByFranchise_shouldReturnList_whenFranchiseExists() {
        List<ProductView> productViews = List.of(
                ProductView.builder().franchiseId(1L).franchiseName("Franchise")
                        .branchId(1L).branchName("Branch").productName("Product A").totalStock(100).build()
        );
        when(franchiseUseCase.getById(1L)).thenReturn(Mono.just(franchise));
        when(productRepository.getTopProductsByFranchise(1L)).thenReturn(Mono.just(productViews));

        StepVerifier.create(productUseCase.getTopStockProductsByFranchise(1L))
                .expectNext(productViews)
                .verifyComplete();
    }

    @Test
    void getTopStockProductsByFranchise_shouldThrowBusinessException_whenFranchiseNotExists() {
        when(franchiseUseCase.getById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.getTopStockProductsByFranchise(99L))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && ((BusinessException) throwable).getErrorMessage() == ErrorMessage.FRANCHISE_DOES_NOT_EXIST)
                .verify();
    }
}
