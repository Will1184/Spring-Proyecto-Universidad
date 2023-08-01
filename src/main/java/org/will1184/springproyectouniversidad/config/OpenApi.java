package org.will1184.springproyectouniversidad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("org.will1184")
                .packagesToScan("org.will1184.springproyectouniversidad.controller.dto")
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Universidad")
                        .description("Registros de universidad api rest ")
                        .version("v2")
                        .license(new License().name("Brandon Gomez").url(""))
                        .contact(new Contact()
                                .name("Brandon Gomez")
                                .email("brandonwilliammg@gmail.com")
                                )
                );
    }
}
