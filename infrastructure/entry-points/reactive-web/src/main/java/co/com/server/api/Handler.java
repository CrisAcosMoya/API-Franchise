package co.com.server.api;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.usecase.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> addFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(franchiseUseCase::createFranchise)
                .flatMap(created -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(created))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(Branch.class)
                .flatMap(newBranch -> franchiseUseCase.addBranchToFranchise(franchiseId, newBranch))
                .flatMap(updated -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updated))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(Product.class)
                .flatMap(newProduct -> franchiseUseCase.addProductToBranch(franchiseId, branchId, newProduct))
                .flatMap(updated -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updated))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return franchiseUseCase.removeProductFromBranch(franchiseId, branchId, productId)
                .flatMap(updated -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updated))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    public Mono<ServerResponse> getHighestStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return franchiseUseCase.getHighestStockProductsForFranchise(franchiseId)
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(StockUpdateRequest.class)
                .flatMap(body -> franchiseUseCase.updateStock(franchiseId, branchId, productId, body.getStock()))
                .flatMap(updated -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updated))
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }
}
