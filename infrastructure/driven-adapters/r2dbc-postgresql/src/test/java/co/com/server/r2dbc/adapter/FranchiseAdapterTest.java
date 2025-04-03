package co.com.server.r2dbc.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import co.com.server.r2dbc.entity.FranchiseEntity;
import co.com.server.r2dbc.repository.FranchiseRepository;
import co.com.server.r2dbc.helper.BranchSerializer;
import co.com.server.r2dbc.helper.FranchiseEntityHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseAdapterTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private FranchiseEntityHelper entityHelper;

    @Mock
    private BranchSerializer branchSerializer;

    @InjectMocks
    private FranchiseAdapter franchiseAdapter;

    @Test
    void testSave() {
        Franchise inputFranchise = new Franchise(null, "Test Franchise", Collections.emptyList());
        when(entityHelper.getBranchSerializer()).thenReturn(branchSerializer);
        when(franchiseRepository.insertFranchise(anyString(), eq("Test Franchise"), anyString()))
                .thenReturn(Mono.empty());
        when(branchSerializer.serialize(Collections.emptyList())).thenReturn("[]");

        Mono<Franchise> result = franchiseAdapter.save(inputFranchise);

        StepVerifier.create(result)
                .assertNext(franchise -> {
                    assertNotNull(franchise.getId());
                    assertEquals("Test Franchise", franchise.getName());
                    assertEquals(Collections.emptyList(), franchise.getBranches());
                })
                .verifyComplete();

        verify(franchiseRepository).insertFranchise(anyString(), eq("Test Franchise"), eq("[]"));
    }

    @Test
    void testAddBranch() {
        String franchiseId = "franchise1";
        Branch newBranch = new Branch("branch1", "New Branch", new ArrayList<>());
        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(new ArrayList<>()));

        List<Branch> updatedBranches = new ArrayList<>();
        updatedBranches.add(newBranch);
        Franchise expectedFranchise = new Franchise(franchiseId, "Test Franchise", updatedBranches);
        when(entityHelper.updateBranches(dummyEntity, updatedBranches)).thenReturn(Mono.just(expectedFranchise));

        Mono<Franchise> result = franchiseAdapter.addBranch(franchiseId, newBranch);

        StepVerifier.create(result)
                .expectNext(expectedFranchise)
                .verifyComplete();
    }

    @Test
    void testAddProduct() {
        String franchiseId = "franchise1";
        String branchId = "branch1";
        Product newProduct = new Product("product1", "Test Product", 100);
        Branch branch = new Branch(branchId, "Test Branch", new ArrayList<>());
        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        branch.getProducts().add(newProduct);
        Franchise expectedFranchise = new Franchise(franchiseId, "Test Franchise", branchList);
        when(entityHelper.updateBranches(dummyEntity, branchList)).thenReturn(Mono.just(expectedFranchise));

        Mono<Franchise> result = franchiseAdapter.addProduct(franchiseId, branchId, newProduct);

        StepVerifier.create(result)
                .expectNext(expectedFranchise)
                .verifyComplete();
    }

    @Test
    void testAddProduct_branchNotFound() {
        String franchiseId = "franchise1";
        String branchId = "nonexistent";
        Product newProduct = new Product("product1", "Test Product", 100);
        List<Branch> branchList = new ArrayList<>();

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        Mono<Franchise> result = franchiseAdapter.addProduct(franchiseId, branchId, newProduct);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }

    @Test
    void testRemoveProduct() {
        String franchiseId = "franchise1";
        String branchId = "branch1";
        String productId = "product1";
        Product product = new Product(productId, "Test Product", 100);
        Branch branch = new Branch(branchId, "Test Branch", new ArrayList<>(List.of(product)));
        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        branch.setProducts(new ArrayList<>()); // queda sin productos
        Franchise expectedFranchise = new Franchise(franchiseId, "Test Franchise", branchList);
        when(entityHelper.updateBranches(dummyEntity, branchList)).thenReturn(Mono.just(expectedFranchise));

        Mono<Franchise> result = franchiseAdapter.removeProduct(franchiseId, branchId, productId);

        StepVerifier.create(result)
                .expectNext(expectedFranchise)
                .verifyComplete();
    }

    @Test
    void testRemoveProduct_branchNotFound() {
        String franchiseId = "franchise1";
        String branchId = "nonexistent";
        String productId = "product1";
        List<Branch> branchList = new ArrayList<>();

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        Mono<Franchise> result = franchiseAdapter.removeProduct(franchiseId, branchId, productId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }

    @Test
    void testGetHighestStockProducts() {
        String franchiseId = "franchise1";
        Product product1 = new Product("product1", "Product1", 50);
        Product product2 = new Product("product2", "Product2", 100);
        Branch branch = new Branch("branch1", "Branch1", new ArrayList<>(Arrays.asList(product1, product2)));
        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        Mono<List<ProductBranch>> result = franchiseAdapter.getHighestStockProducts(franchiseId);

        StepVerifier.create(result)
                .assertNext(productBranches -> {
                    assertEquals(1, productBranches.size());
                    ProductBranch pb = productBranches.get(0);
                    assertEquals("branch1", pb.getBranchId());
                    assertEquals("Product2", pb.getProductName());
                })
                .verifyComplete();
    }

    @Test
    void testUpdateProductStock() {
        String franchiseId = "franchise1";
        String branchId = "branch1";
        String productId = "product1";
        int newStock = 200;
        Product product = new Product(productId, "Product1", 50);
        Branch branch = new Branch(branchId, "Branch1", new ArrayList<>(List.of(product)));
        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        product.setStock(newStock);
        Franchise expectedFranchise = new Franchise(franchiseId, "Test Franchise", branchList);
        when(entityHelper.updateBranches(dummyEntity, branchList)).thenReturn(Mono.just(expectedFranchise));

        Mono<Franchise> result = franchiseAdapter.updateProductStock(franchiseId, branchId, productId, newStock);

        StepVerifier.create(result)
                .expectNext(expectedFranchise)
                .verifyComplete();
    }

    @Test
    void testUpdateProductStock_branchNotFound() {
        String franchiseId = "franchise1";
        String branchId = "nonexistent";
        String productId = "product1";
        List<Branch> branchList = new ArrayList<>();

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        Mono<Franchise> result = franchiseAdapter.updateProductStock(franchiseId, branchId, productId, 100);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }

    @Test
    void testUpdateProductStock_productNotFound() {
        String franchiseId = "franchise1";
        String branchId = "branch1";
        String productId = "nonexistent";
        Branch branch = new Branch(branchId, "Branch1", new ArrayList<>());
        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        FranchiseEntity dummyEntity = new FranchiseEntity();
        dummyEntity.setId(franchiseId);
        dummyEntity.setName("Test Franchise");
        dummyEntity.setBranches("[]");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(dummyEntity));
        when(entityHelper.getBranches(dummyEntity)).thenReturn(Mono.just(branchList));

        Mono<Franchise> result = franchiseAdapter.updateProductStock(franchiseId, branchId, productId, 100);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Producto no encontrado en la sucursal"))
                .verify();
    }
}