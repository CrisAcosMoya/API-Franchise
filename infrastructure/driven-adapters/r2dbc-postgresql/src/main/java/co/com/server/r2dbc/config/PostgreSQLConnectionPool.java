package co.com.server.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PostgreSQLConnectionPool {
    public static final int INITIAL_SIZE = 12;
    public static final int MAX_SIZE = 15;
    public static final int MAX_IDLE_TIME = 30;

    @Bean
    public ConnectionPool connectionPool(PostgresqlConnectionProperties properties) {
        String url = String.format("r2dbc:postgresql://%s:%d/%s?schema=%s",
                properties.host(),
                properties.port(),
                properties.database(),
                properties.schema());

        ConnectionFactory connectionFactory = ConnectionFactoryBuilder.withUrl(url)
                .username(properties.username())
                .password(properties.password())
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(connectionFactory)
                .name("api-postgres-connection-pool")
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}