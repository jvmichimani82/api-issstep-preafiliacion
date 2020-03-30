package issstep.afiliacion.config;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;


import io.swagger.models.ExternalDocs;
import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;
import io.swagger.models.auth.BasicAuthDefinition;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig /*implements ReaderListener*/ {

	 final List<ResponseMessage> globalResponses = Arrays.asList(
		        new ResponseMessageBuilder()
		            .code(201)
		            .message("Creado")
		            .build(),
	            new ResponseMessageBuilder()
		            .code(401)
		            .message("No Autorizado")
		            .build(),
		        new ResponseMessageBuilder()
		            .code(409)
		            .message("Conflicto")
		            .build(),
		        new ResponseMessageBuilder()
		            .code(500)
		            .message("Error del servidor")
		            .build());
	
	
	@Bean
	public Docket postsApi() {
	return new Docket(DocumentationType.SWAGGER_2)
	.apiInfo(apiInfo()).useDefaultResponseMessages(false)
    .globalResponseMessage(RequestMethod.GET, globalResponses)
    .globalResponseMessage(RequestMethod.POST, globalResponses)
    .globalResponseMessage(RequestMethod.DELETE, globalResponses)
    .globalResponseMessage(RequestMethod.PUT, globalResponses)
    .select()
	 .apis(RequestHandlerSelectors.basePackage("issstep.afiliacion.controller"))
	               .paths(PathSelectors.any()).build().securitySchemes(Collections.singletonList(apiKey()));
	}
	
	private ApiInfo apiInfo() {
	     return new ApiInfoBuilder()
	             .title("Afiliacion")
	             .description("Afiliacion Issstep")
	             .termsOfServiceUrl("")
	             .version("1.0")
	             .build();
	 }
 
	private ApiKey apiKey() {
	    return new ApiKey("Authorization", "Authorization", "header");
	}
	
	 
}