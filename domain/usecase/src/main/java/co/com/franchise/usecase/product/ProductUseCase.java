package co.com.franchise.usecase.product;

import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import co.com.franchise.model.product.gateways.ProductRepository;
import co.com.franchise.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;
    private final BranchUseCase branchUseCase;

    public Mono<Product> create(ProductParam productParam) {
        return branchUseCase.get(productParam.getBranchId())
                .flatMap(branch -> productRepository.save(productParam))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.BRANCH_DOES_NOT_EXIST)));
    }
}
