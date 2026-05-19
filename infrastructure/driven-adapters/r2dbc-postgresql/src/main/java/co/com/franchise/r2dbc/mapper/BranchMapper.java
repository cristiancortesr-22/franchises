package co.com.franchise.r2dbc.mapper;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import co.com.franchise.r2dbc.model.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BranchMapper {

    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    @Mapping(target = "id", ignore = true)
    BranchEntity toBranchEntity(BranchParam branch);
    BranchEntity toBranchEntity(Branch branch);
    Branch toBranch(BranchEntity branchEntity);
}
