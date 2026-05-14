package co.com.franchise.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
public class PostgreSQLConnectionPool {

    @Bean
    public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties,
                                              PostgreSQLConnectionPoolProperties connectionPoolProperties) {

        var connection = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, properties.getDriver())
                .option(HOST, properties.getHost())
                .option(PORT, properties.getPort())
                .option(DATABASE, properties.getDatabase())
                .option(USER, properties.getUsername())
                .option(PASSWORD, properties.getPassword())
                .option(ConnectionFactoryOptions.SSL, false)
                .build());

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connection)
                .name("api-postgres-connection-pool")
                .initialSize(connectionPoolProperties.getInitialSize())
                .maxSize(connectionPoolProperties.getMaxSize())
                .maxIdleTime(Duration.ofMinutes(connectionPoolProperties.getMaxIdleTime()))
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}
