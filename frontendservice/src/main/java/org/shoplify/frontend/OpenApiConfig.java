package org.shoplify.frontend;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shoplify API")
                        .version("1.0.0")
                        .description("API for interacting with Shoplify backend"))
                .servers(Collections.singletonList(
                        new Server()
                                .url("https://{app_host}/")
                                .description("Base URL for the API")
                ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**") // Adjust path pattern as needed
                .build();
    }


}