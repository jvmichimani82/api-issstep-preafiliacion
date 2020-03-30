package issstep.afiliacion.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ApplicationConfig extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfig.class, args);
	}
}