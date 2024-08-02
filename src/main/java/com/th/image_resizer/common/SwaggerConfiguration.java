package com.th.image_resizer.common;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components())
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("img resize")
                        .description("이미지 조절")
                        .version("1.0.0")
                );
    }

}