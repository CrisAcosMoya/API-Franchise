package co.com.server.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import co.com.server.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    @Test
    void createFranchise_shouldSetIdAndCallSave() {
        Franchise inputFranchise = new Franchise(null, "FranchiseName", new ArrayList<>());
        Franchise savedFranchise = new Franchise("generated-uuid", "FranchiseName", new ArrayList<>());
        when(franchiseGateway.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        Mono<Franchise> result = franchiseUseCase.createFranchise(inputFranchise);

        StepVerifier.create(result)
                .assertNext(franchise -> {
                    assertNotNull(franchise.getId());
                    assertEquals("FranchiseName", franchise.getName());
                })
                .verifyComplete();

        verify(franchiseGateway, times(1)).save(any(Franchise.class));
    }

    @Test
    void addBranchToFranchise_shouldCallGateway() {
        String franchiseId = "franchise-1";
        Branch branch = new Branch("branch-1", "BranchName", new ArrayList<>());
        Franchise franchise = new Franchise(franchiseId, "FranchiseName", List.of(branch));
        when(franchiseGateway.addBranch(franchiseId, branch)).thenReturn(Mono.just(franchise));

        Mono<Franchise> result = franchiseUseCase.addBranchToFranchise(franchiseId, branch);

        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway, times(1)).addBranch(franchiseId, branch);
    }

    @Test
    void addProductToBranch_shouldGenerateIdIfMissingAndCallGateway() {
        String franchiseId = "franchise-1";
        String branchId = "branch-1";
        Product product = new Product(null, "ProductName", 10);
        Franchise franchise = new Franchise(franchiseId, "FranchiseName", new ArrayList<>());

        when(franchiseGateway.addProduct(eq(franchiseId), eq(branchId), any(Product.class)))
                .thenReturn(Mono.just(franchise));

        Mono<Franchise> result = franchiseUseCase.addProductToBranch(franchiseId, branchId, product);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getName().equals("FranchiseName"))
                .verifyComplete();

        verify(franchiseGateway, times(1)).addProduct(eq(franchiseId), eq(branchId), any(Product.class));
        assertNotNull(product.getId(), "El producto debe tener un ID asignado");
    }

    @Test
    void removeProductFromBranch_shouldCallGateway() {
        String franchiseId = "franchise-1";
        String branchId = "branch-1";
        String productId = "product-1";
        Franchise franchise = new Franchise(franchiseId, "FranchiseName", new ArrayList<>());

        when(franchiseGateway.removeProduct(franchiseId, branchId, productId))
                .thenReturn(Mono.just(franchise));

        Mono<Franchise> result = franchiseUseCase.removeProductFromBranch(franchiseId, branchId, productId);

        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway, times(1)).removeProduct(franchiseId, branchId, productId);
    }

    @Test
    void getHighestStockProductsForFranchise_shouldCallGateway() {
        String franchiseId = "franchise-1";
        List<ProductBranch> productBranches = Arrays.asList(
                new ProductBranch("branch-1", "Branch1", "prod-1", "Product1", 100),
                new ProductBranch("branch-2", "Branch2", "prod-2", "Product2", 200)
        );
        when(franchiseGateway.getHighestStockProducts(franchiseId))
                .thenReturn(Mono.just(productBranches));

        Mono<List<ProductBranch>> result = franchiseUseCase.getHighestStockProductsForFranchise(franchiseId);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertEquals(2, list.size());
                    assertEquals("Product2", list.get(1).getProductName());
                })
                .verifyComplete();

        verify(franchiseGateway, times(1)).getHighestStockProducts(franchiseId);
    }

    @Test
    void updateStock_shouldCallGateway() {
        String franchiseId = "franchise-1";
        String branchId = "branch-1";
        String productId = "product-1";
        int newStock = 50;
        Franchise franchise = new Franchise(franchiseId, "FranchiseName", new ArrayList<>());

        when(franchiseGateway.updateProductStock(franchiseId, branchId, productId, newStock))
                .thenReturn(Mono.just(franchise));

        Mono<Franchise> result = franchiseUseCase.updateStock(franchiseId, branchId, productId, newStock);

        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway, times(1)).updateProductStock(franchiseId, branchId, productId, newStock);
    }
}