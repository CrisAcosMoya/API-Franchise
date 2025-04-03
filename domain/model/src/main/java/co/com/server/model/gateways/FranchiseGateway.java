package co.com.server.model.gateways;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FranchiseGateway {

    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> addBranch(String franchiseId, Branch branch);
    Mono<Franchise> addProduct(String franchiseId, String branchId, Product product);
    Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId);
    Mono<List<ProductBranch>> getHighestStockProducts(String franchiseId);
    Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int newStock);
}
