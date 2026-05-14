package co.com.franchise.r2dbc.mapper;

import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import co.com.franchise.model.product.ProductView;
import co.com.franchise.r2dbc.model.ProductEntity;
import co.com.franchise.r2dbc.model.dto.ProductViewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    ProductEntity toProductEntity(ProductParam productParam);

    Product toProduct(ProductEntity productEntity);

    ProductView toProductView(ProductViewDTO productViewDTO);
}
