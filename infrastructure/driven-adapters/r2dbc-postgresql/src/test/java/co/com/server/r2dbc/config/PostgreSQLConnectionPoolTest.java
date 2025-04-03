package co.com.server.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgreSQLConnectionPoolTest {

    @InjectMocks
    private PostgreSQLConnectionPool connectionPoolConfig;

    @Mock
    private PostgresqlConnectionProperties properties;

    @BeforeEach
    void setUp() {
        when(properties.host()).thenReturn("localhost");
        when(properties.port()).thenReturn(5432);
        when(properties.database()).thenReturn("dbName");
        when(properties.schema()).thenReturn("schema");
        when(properties.username()).thenReturn("username");
        when(properties.password()).thenReturn("password");
    }

    @Test
    void testConnectionPoolBeanCreation() {
        ConnectionPool pool = connectionPoolConfig.connectionPool(properties);
        assertNotNull(pool);
    }
}
