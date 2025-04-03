package co.com.server.config;

import co.com.server.model.Branch;
import co.com.server.model.Franchise;
import co.com.server.model.Product;
import co.com.server.model.ProductBranch;
import co.com.server.model.gateways.FranchiseGateway;
import co.com.server.usecase.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        FranchiseUseCase franchiseUseCase() {return  new FranchiseUseCase(new FranchiseGateway() {
            @Override
            public Mono<Franchise> save(Franchise franchise) {
                return null;
            }

            @Override
            public Mono<Franchise> addBranch(String franchiseId, Branch branch) {
                return null;
            }

            @Override
            public Mono<Franchise> addProduct(String franchiseId, String branchId, Product product) {
                return null;
            }

            @Override
            public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
                return null;
            }

            @Override
            public Mono<List<ProductBranch>> getHighestStockProducts(String franchiseId) {
                return null;
            }

            @Override
            public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int newStock) {
                return null;
            }
        });}
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}