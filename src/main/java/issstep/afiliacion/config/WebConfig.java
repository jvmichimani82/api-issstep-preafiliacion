package issstep.afiliacion.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import issstep.afiliacion.security.JwtAccesDeniedHandler;
import issstep.afiliacion.security.JwtAuthenticationEntryPoint;
import issstep.afiliacion.security.JwtTokenUtil;
import issstep.afiliacion.service.SessionService;




@Configuration
@EnableWebMvc
@ComponentScan({ "issstep.afiliacion" })
public class WebConfig extends WebMvcConfigurerAdapter {
    	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    @Bean 
	public ShaPasswordEncoder passwordEncoder() throws Exception {  
		return new ShaPasswordEncoder(256);  
	}
	
	@Bean 
	public SessionService sessionService() throws Exception {  
		return new SessionService();  
	}
	
	
	@Bean
    public JwtAuthenticationEntryPoint unauthorizedHandler() throws Exception {
        return new JwtAuthenticationEntryPoint();
    }
	
	@Bean
    public JwtAccesDeniedHandler accesDeniedHandler() throws Exception {
        return new JwtAccesDeniedHandler();
    }
	
	@Bean
    public JwtTokenUtil jwtTokenUtil() throws Exception {
        return new JwtTokenUtil();
    }
	
   @Bean
    public JavaMailSender mailSender() throws IOException {
        Properties properties = configProperties();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(properties.getProperty("mail.host"));
        mailSender.setPort(Integer.parseInt(properties.getProperty("mail.port")));
        //mailSender.setProtocol(properties.getProperty("mail.server.protocol"));
        mailSender.setUsername(properties.getProperty("mail.username"));
        mailSender.setPassword(properties.getProperty("mail.password"));
        mailSender.setJavaMailProperties(javaMailProperties());
        return mailSender;
        
    }
    
    private Properties configProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("application.properties").getInputStream());
        return properties;
    }

    private Properties javaMailProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("javaMail.properties").getInputStream());
        return properties;
    }
    
    @Bean(name = "mysqlDb")
	@ConfigurationProperties(prefix = "spring.ds_mysql")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "mysqlJdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("mysqlDb") DataSource dsMySQL) {
		return new JdbcTemplate(dsMySQL);
	}
	

}