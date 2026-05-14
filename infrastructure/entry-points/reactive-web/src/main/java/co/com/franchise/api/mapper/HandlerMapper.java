package co.com.franchise.api.mapper;

import co.com.franchise.api.branch.dto.BranchRequest;
import co.com.franchise.api.branch.dto.BranchResponse;
import co.com.franchise.api.franchise.dto.FranchiseRequest;
import co.com.franchise.api.franchise.dto.FranchiseResponse;
import co.com.franchise.api.product.dto.ProductRequest;
import co.com.franchise.api.product.dto.ProductResponse;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HandlerMapper {
    HandlerMapper MAPPER = Mappers.getMapper(HandlerMapper.class);

    FranchiseParam toFranchiseParam(FranchiseRequest request);
    FranchiseResponse toFranchiseResponse(Franchise franchise);
    BranchParam toBranchParam(BranchRequest branchRequest);
    BranchResponse toBranchResponse(Branch branch);
    ProductParam toProductParam(ProductRequest productRequest);
    ProductResponse toProductResponse(Product product);
}
