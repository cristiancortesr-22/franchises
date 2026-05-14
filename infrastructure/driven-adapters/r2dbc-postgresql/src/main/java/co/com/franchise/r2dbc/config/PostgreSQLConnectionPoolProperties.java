package co.com.franchise.r2dbc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "infrastructure.db.postgres.pool")
public class PostgreSQLConnectionPoolProperties {
    private int initialSize;
    private int maxSize;
    private int maxIdleTime;
}
