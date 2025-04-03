package co.com.server.r2dbc.helper;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.r2dbc.entity.FranchiseEntity;
import co.com.server.r2dbc.repository.FranchiseRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FranchiseEntityHelper {

    @Getter
    private final BranchSerializer branchSerializer;
    private final FranchiseRepository franchiseRepository;

    public Mono<List<Branch>> getBranches(FranchiseEntity entity) {
        return Mono.just(branchSerializer.deserialize(entity.getBranches()));
    }

    public Franchise mapToFranchise(FranchiseEntity entity, List<Branch> branches) {
        return new Franchise(entity.getId(), entity.getName(), branches);
    }

    public Mono<Franchise> updateBranches(FranchiseEntity entity, List<Branch> branches) {
        String updatedBranches = branchSerializer.serialize(branches);
        return franchiseRepository.updateBranches(entity.getId(), updatedBranches)
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated > 0) {
                        return Mono.just(mapToFranchise(entity, branches));
                    } else {
                        return Mono.error(new RuntimeException("Error al actualizar la franquicia"));
                    }
                });
    }
}

