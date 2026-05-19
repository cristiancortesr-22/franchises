package co.com.franchise.r2dbc.mapper;

import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.FranchiseParam;
import co.com.franchise.r2dbc.model.FranchiseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FranchiseMapper {

    FranchiseMapper INSTANCE = Mappers.getMapper(FranchiseMapper.class);

    @Mapping(target = "id", ignore = true)
    FranchiseEntity toFranchiseEntity(FranchiseParam franchiseParam);

    Franchise toFranchise(FranchiseEntity entity);

    FranchiseEntity toFranchiseEntity(Franchise franchise);
}
