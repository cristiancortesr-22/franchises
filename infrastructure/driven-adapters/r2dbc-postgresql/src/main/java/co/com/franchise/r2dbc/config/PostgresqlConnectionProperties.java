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
@ConfigurationProperties(prefix = "infrastructure.db.postgres")
public class PostgresqlConnectionProperties {
    private String host;
    private Integer port;
    private String database;
    private String schema;
    private String username;
    private String password;
    private String driver;
}
