package com.backtrader.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@SecurityScheme(name = "Authorization", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER, 
                  bearerFormat = "Bearer {token}")
public class SwaggerConfig {
	@Bean
	GroupedOpenApi openapi() {
		return GroupedOpenApi.builder().group("BackTrader Application").displayName("BackTrader Application")
				.pathsToMatch().packagesToScan("com.backtrader").build();
	}

	@Bean
	OpenAPI api() {
		return new OpenAPI().info(new Info().title("BackTrader Application")
				.description("Tarding Application to display the trading details of an user").contact(new Contact())
				.title("BackTrader Application").version("1.0"));
	}

}
