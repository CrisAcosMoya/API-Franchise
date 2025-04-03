package co.com.server.usecase;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.gateways.FranchiseGateway;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        franchise.setId(UUID.randomUUID().toString());
        return franchiseGateway.save(franchise);
    }

    public Mono<Franchise> addBranchToFranchise(String franchiseId, Branch branch) {
        return franchiseGateway.addBranch(franchiseId, branch);
    }

    public Mono<Franchise> addProductToBranch(String franchiseId, String branchId, Product product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(java.util.UUID.randomUUID().toString());
        }
        return franchiseGateway.addProduct(franchiseId, branchId, product);
    }

    public Mono<Franchise> removeProductFromBranch(String franchiseId, String branchId, String productId) {
        return franchiseGateway.removeProduct(franchiseId, branchId, productId);
    }

    public Mono<List<ProductBranch>> getHighestStockProductsForFranchise(String franchiseId) {
        return franchiseGateway.getHighestStockProducts(franchiseId);
    }

    public Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, int newStock) {
        return franchiseGateway.updateProductStock(franchiseId, branchId, productId, newStock);
    }
}
