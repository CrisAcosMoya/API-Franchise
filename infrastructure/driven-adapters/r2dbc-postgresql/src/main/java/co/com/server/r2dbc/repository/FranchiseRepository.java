package co.com.server.r2dbc.repository;

import co.com.server.r2dbc.entity.FranchiseEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, String>,
        ReactiveQueryByExampleExecutor<Object> {

    @Modifying
    @Query("INSERT INTO franchise (id, name, branches) VALUES (:id, :name, :branches)")
    Mono<Void> insertFranchise(@Param("id") String id,
                               @Param("name") String name,
                               @Param("branches") String branches);

    @Modifying
    @Query("UPDATE franchise SET branches = :branches WHERE id = :id")
    Mono<Integer> updateBranches(@Param("id") String id, @Param("branches") String branches);
}
