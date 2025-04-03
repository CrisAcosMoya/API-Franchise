package co.com.server.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import co.com.server.usecase.FranchiseUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testAddFranchise() throws Exception {
        Franchise inputFranchise = new Franchise(null, "Franchise A", new ArrayList<>());
        Franchise createdFranchise = new Franchise("1", "Franchise A", new ArrayList<>());

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(createdFranchise));

        webTestClient.post()
                .uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(inputFranchise))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Franchise.class)
                .value(response -> {
                    assertEquals("1", response.getId());
                    assertEquals("Franchise A", response.getName());
                });
    }

    @Test
    void testAddBranch() throws Exception {
        String franchiseId = "1";
        Branch newBranch = new Branch("b1", "Branch One", new ArrayList<>());
        Franchise updatedFranchise = new Franchise(franchiseId, "Franchise A", List.of(newBranch));

        when(franchiseUseCase.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(updatedFranchise));

        webTestClient.post()
                .uri("/api/franchise/{franchiseId}/branch", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(newBranch))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Franchise.class)
                .value(response -> {
                    assertEquals(franchiseId, response.getId());
                    assertEquals("Franchise A", response.getName());
                    assertEquals(1, response.getBranches().size());
                    assertEquals("b1", response.getBranches().get(0).getId());
                });
    }

    @Test
    void testAddProduct() throws Exception {
        String franchiseId = "1";
        String branchId = "b1";
        Product newProduct = new Product("p1", "Product One", 100);
        Franchise updatedFranchise = new Franchise(franchiseId, "Franchise A", new ArrayList<>());

        when(franchiseUseCase.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                .thenReturn(Mono.just(updatedFranchise));

        webTestClient.post()
                .uri("/api/franchise/{franchiseId}/branch/{branchId}/product", franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(newProduct))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Franchise.class)
                .value(response -> {
                    assertEquals(franchiseId, response.getId());
                    assertEquals("Franchise A", response.getName());
                });
    }

    @Test
    void testRemoveProduct() {
        String franchiseId = "1";
        String branchId = "b1";
        String productId = "p1";
        Franchise updatedFranchise = new Franchise(franchiseId, "Franchise A", new ArrayList<>());

        when(franchiseUseCase.removeProductFromBranch(franchiseId, branchId, productId))
                .thenReturn(Mono.just(updatedFranchise));

        webTestClient.delete()
                .uri("/api/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                        franchiseId, branchId, productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Franchise.class)
                .value(response -> {
                    assertEquals(franchiseId, response.getId());
                });
    }

    @Test
    void testGetHighestStockProducts() {
        String franchiseId = "1";
        List<ProductBranch> productBranches = List.of(
                new ProductBranch("b1", "Branch One", "p1", "Product One", 150)
        );

        when(franchiseUseCase.getHighestStockProductsForFranchise(franchiseId))
                .thenReturn(Mono.just(productBranches));

        webTestClient.get()
                .uri("/api/franchise/{franchiseId}/highest-stock", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductBranch.class)
                .value(response -> {
                    assertEquals(1, response.size());
                    assertEquals("Branch One", response.get(0).getBranchName());
                    assertEquals("Product One", response.get(0).getProductName());
                    assertEquals(150, response.get(0).getStock());
                });
    }

    @Test
    void testUpdateStock() throws Exception {
        String franchiseId = "1";
        String branchId = "b1";
        String productId = "p1";
        StockUpdateRequest updateRequest = new StockUpdateRequest();
        updateRequest.setStock(200);

        Franchise updatedFranchise = new Franchise(franchiseId, "Franchise A", new ArrayList<>());

        when(franchiseUseCase.updateStock(eq(franchiseId), eq(branchId), eq(productId), eq(200)))
                .thenReturn(Mono.just(updatedFranchise));

        webTestClient.put()
                .uri("/api/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock",
                        franchiseId, branchId, productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(updateRequest))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Franchise.class)
                .value(response -> {
                    assertEquals(franchiseId, response.getId());
                });
    }
}

