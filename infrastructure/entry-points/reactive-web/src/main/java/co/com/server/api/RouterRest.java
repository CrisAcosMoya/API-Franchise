package co.com.server.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/franchise"), handler::addFranchise)
                .andRoute(POST("/api/franchise/{franchiseId}/branch"), handler::addBranch)
                .andRoute(POST("/api/franchise/{franchiseId}/branch/{branchId}/product"), handler::addProduct)
                .andRoute(DELETE("/api/franchise/{franchiseId}/branch/{branchId}/product/{productId}"),
                        handler::removeProduct)
                .andRoute(GET("/api/franchise/{franchiseId}/highest-stock"), handler::getHighestStockProducts)
                .andRoute(PUT("/api/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock"),
                        handler::updateStock);
    }
}
