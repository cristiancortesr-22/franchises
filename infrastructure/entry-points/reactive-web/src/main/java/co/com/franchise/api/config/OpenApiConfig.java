package co.com.franchise.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise Management API")
                        .version("1.0.0")
                        .description("API for managing a franchise network. Each franchise has branches, and each branch has products with stock management.")
                        .contact(new Contact()
                                .name("Franchise API")
                                .email("dev@franchise.com")))
                .servers(List.of(
                        new Server().url("/api/v1").description("Local server")
                ));
    }
}
