package co.com.server.r2dbc.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.r2dbc.entity.FranchiseEntity;
import co.com.server.r2dbc.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseEntityHelperTest {

    @Mock
    private BranchSerializer branchSerializer;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseEntityHelper entityHelper;

    private FranchiseEntity franchiseEntity;
    private List<Branch> dummyBranches;

    @BeforeEach
    void setUp() {
        String branchesJson = "[{\"id\":\"branch1\",\"name\":\"Sucursal Uno\",\"products\":[]}]";
        franchiseEntity = new FranchiseEntity("franchise1", "Test Franchise", branchesJson);

        Branch branch = new Branch("branch1", "Sucursal Uno", new ArrayList<>());
        dummyBranches = List.of(branch);
    }

    @Test
    void getBranches_ReturnsDeserializedBranches() {
        when(branchSerializer.deserialize(franchiseEntity.getBranches())).thenReturn(dummyBranches);

        Mono<List<Branch>> result = entityHelper.getBranches(franchiseEntity);

        StepVerifier.create(result)
                .assertNext(branches -> {
                    assertNotNull(branches);
                    assertEquals(1, branches.size());
                    assertEquals("branch1", branches.get(0).getId());
                })
                .verifyComplete();
    }

    @Test
    void mapToFranchise_ReturnsMappedFranchise() {
        Franchise franchise = entityHelper.mapToFranchise(franchiseEntity, dummyBranches);

        assertNotNull(franchise);
        assertEquals(franchiseEntity.getId(), franchise.getId());
        assertEquals(franchiseEntity.getName(), franchise.getName());
        assertEquals(dummyBranches, franchise.getBranches());
    }

    @Test
    void updateBranches_Success_ReturnsUpdatedFranchise() {
        String serializedBranches = "[{\"id\":\"branch1\",\"name\":\"Sucursal Uno\",\"products\":[]}]";
        when(branchSerializer.serialize(dummyBranches)).thenReturn(serializedBranches);
        when(franchiseRepository.updateBranches(eq(franchiseEntity.getId()), eq(serializedBranches)))
                .thenReturn(Mono.just(1));

        Mono<Franchise> result = entityHelper.updateBranches(franchiseEntity, dummyBranches);

        StepVerifier.create(result)
                .assertNext(franchise -> {
                    assertNotNull(franchise);
                    assertEquals(franchiseEntity.getId(), franchise.getId());
                    assertEquals(franchiseEntity.getName(), franchise.getName());
                    assertEquals(dummyBranches, franchise.getBranches());
                })
                .verifyComplete();
    }

    @Test
    void updateBranches_Failure_ReturnsError() {
        String serializedBranches = "[{\"id\":\"branch1\",\"name\":\"Sucursal Uno\",\"products\":[]}]";
        when(branchSerializer.serialize(dummyBranches)).thenReturn(serializedBranches);
        when(franchiseRepository.updateBranches(eq(franchiseEntity.getId()), eq(serializedBranches)))
                .thenReturn(Mono.just(0));

        Mono<Franchise> result = entityHelper.updateBranches(franchiseEntity, dummyBranches);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error al actualizar la franquicia"))
                .verify();
    }
}