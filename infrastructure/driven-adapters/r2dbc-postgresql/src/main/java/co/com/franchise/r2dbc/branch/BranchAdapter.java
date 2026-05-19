package co.com.franchise.r2dbc.branch;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.BranchParam;
import co.com.franchise.model.branch.gateways.BranchRepository;
import co.com.franchise.model.enums.ErrorMessage;
import co.com.franchise.model.exceptions.BusinessException;
import co.com.franchise.model.exceptions.InfrastructureException;
import co.com.franchise.r2dbc.mapper.BranchMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class BranchAdapter implements BranchRepository {

    private final MyBranchRepository myBranchRepository;
    private final CircuitBreaker databaseCircuitBreaker;

    @Override
    public Mono<Branch> save(BranchParam branchParam) {
        return myBranchRepository.save(BranchMapper.INSTANCE.toBranchEntity(branchParam))
                .map(BranchMapper.INSTANCE::toBranch)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Save branch request", kv("saveBranchRequest", branchParam)))
                .doOnSuccess(branch -> log.info("Saved branch response", kv("savedBranchResponse", branch)))
                .doOnError(throwable -> log.error("Save branch error", throwable))
                .onErrorMap(DuplicateKeyException.class, error ->
                        new BusinessException(ErrorMessage.BRANCH_ALREADY_EXISTS))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.BRANCH_CREATION_FAILED));
    }

    @Override
    public Mono<Branch> get(Long id) {
        return myBranchRepository.findById(id)
                .map(BranchMapper.INSTANCE::toBranch)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Get branch request",
                        kv("getBranchRequest", Map.of("id", id))))
                .doOnSuccess(branch -> log.info("Get branch response", kv("getBranchResponse", branch)))
                .doOnError(throwable -> log.error("Get branch error", throwable))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.BRANCH_GET_FAILED));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        return myBranchRepository.save(BranchMapper.INSTANCE.toBranchEntity(branch))
                .map(BranchMapper.INSTANCE::toBranch)
                .transformDeferred(CircuitBreakerOperator.of(databaseCircuitBreaker))
                .doOnSubscribe(subscription -> log.info("Update branch request", kv("updateBranchRequest", branch)))
                .doOnSuccess(branchResponse -> log.info("Updated branch response", kv("updatedBranchResponse", branchResponse)))
                .doOnError(throwable -> log.error("Update branch error", throwable))
                .onErrorMap(DuplicateKeyException.class, error ->
                        new BusinessException(ErrorMessage.BRANCH_ALREADY_EXISTS_WITH_THIS_NAME))
                .onErrorMap(TransientDataAccessException.class, error ->
                        new InfrastructureException(error, ErrorMessage.BRANCH_CREATION_FAILED));
    }
}
