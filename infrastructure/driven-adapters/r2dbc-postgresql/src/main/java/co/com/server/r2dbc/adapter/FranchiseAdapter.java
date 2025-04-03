package co.com.server.r2dbc.adapter;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.gateways.FranchiseGateway;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import co.com.server.r2dbc.repository.FranchiseRepository;
import co.com.server.r2dbc.helper.FranchiseEntityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FranchiseAdapter implements FranchiseGateway {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityHelper entityHelper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        List<Branch> branches = franchise.getBranches() != null ? franchise.getBranches() : Collections.emptyList();
        String franchiseId = franchise.getId() != null ? franchise.getId() : UUID.randomUUID().toString();

        return franchiseRepository.insertFranchise(franchiseId, franchise.getName(),
                        entityHelper.getBranchSerializer().serialize(branches))
                .then(Mono.just(new Franchise(franchiseId, franchise.getName(), branches)));
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, Branch newBranch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(entity -> entityHelper.getBranches(entity)
                        .flatMap(branches -> {
                            branches.add(newBranch);
                            return entityHelper.updateBranches(entity, branches);
                        }));
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchId, Product newProduct) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(entity -> entityHelper.getBranches(entity)
                        .flatMap(branches -> {
                            boolean branchFound = false;
                            for (Branch branch : branches) {
                                if (branchId.equals(branch.getId())) {
                                    List<Product> products = branch.getProducts() != null ? branch.getProducts() : new ArrayList<>();
                                    products.add(newProduct);
                                    branch.setProducts(products);
                                    branchFound = true;
                                    break;
                                }
                            }
                            if (!branchFound) {
                                return Mono.error(new RuntimeException("Sucursal no encontrada"));
                            }
                            return entityHelper.updateBranches(entity, branches);
                        }));
    }

    @Override
    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(entity -> entityHelper.getBranches(entity)
                        .flatMap(branches -> {
                            boolean branchFound = false;
                            for (Branch branch : branches) {
                                if (branchId.equals(branch.getId())) {
                                    branchFound = true;
                                    if (branch.getProducts() != null) {
                                        List<Product> updatedProducts = branch.getProducts().stream()
                                                .filter(product -> !productId.equals(product.getId()))
                                                .collect(Collectors.toList());
                                        branch.setProducts(updatedProducts);
                                    }
                                    break;
                                }
                            }
                            if (!branchFound) {
                                return Mono.error(new RuntimeException("Sucursal no encontrada"));
                            }
                            return entityHelper.updateBranches(entity, branches);
                        }));
    }

    @Override
    public Mono<List<ProductBranch>> getHighestStockProducts(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(entity -> entityHelper.getBranches(entity)
                        .map(branches -> {
                            List<ProductBranch> result = new ArrayList<>();
                            for (Branch branch : branches) {
                                if (branch.getProducts() != null && !branch.getProducts().isEmpty()) {
                                    Product maxProduct = branch.getProducts().stream()
                                            .max(Comparator.comparingInt(Product::getStock))
                                            .orElse(null);
                                    result.add(new ProductBranch(
                                            branch.getId(),
                                            branch.getName(),
                                            maxProduct.getId(),
                                            maxProduct.getName(),
                                            maxProduct.getStock()));
                                }
                            }
                            return result;
                        }));
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int newStock) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(entity -> entityHelper.getBranches(entity)
                        .flatMap(branches -> {
                            boolean branchFound = false;
                            boolean productFound = false;
                            for (Branch branch : branches) {
                                if (branchId.equals(branch.getId())) {
                                    branchFound = true;
                                    if (branch.getProducts() != null) {
                                        for (Product product : branch.getProducts()) {
                                            if (productId.equals(product.getId())) {
                                                product.setStock(newStock);
                                                productFound = true;
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            if (!branchFound) {
                                return Mono.error(new RuntimeException("Sucursal no encontrada"));
                            }
                            if (!productFound) {
                                return Mono.error(new RuntimeException("Producto no encontrado en la sucursal"));
                            }
                            return entityHelper.updateBranches(entity, branches);
                        }));
    }
}
