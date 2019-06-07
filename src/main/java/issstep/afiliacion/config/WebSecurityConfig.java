package issstep.afiliacion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import issstep.afiliacion.security.JwtAccesDeniedHandler;
import issstep.afiliacion.security.JwtAuthenticationEntryPoint;
import issstep.afiliacion.security.JwtAuthenticationTokenFilter;
import issstep.afiliacion.security.UserPermissionEvaluator;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	//@Autowired
	//private SessionService sessionService;
	@Autowired
	private ShaPasswordEncoder passwordEncoder;
	
    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sessionService).passwordEncoder(passwordEncoder);
        
    }
    
     protected MethodSecurityExpressionHandler createExpressionHandler() {
      DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
      expressionHandler.setPermissionEvaluator(new UserPermissionEvaluator(sessionService));
      return expressionHandler;
    }*/
    
    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter{
    	
    	@Autowired
        private JwtAuthenticationEntryPoint unauthorizedHandler;
    	
    	@Autowired
        private JwtAccesDeniedHandler accesDeniedHandler;
    	
    	@Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
            JwtAuthenticationTokenFilter authenticationTokenFilter = new JwtAuthenticationTokenFilter();
            authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
            return authenticationTokenFilter;
        }
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
            	.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).accessDeniedHandler(accesDeniedHandler).and()
	    	    // don't create session
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	            	.antMatcher("/**")
	            	.authorizeRequests()
	            	.antMatchers("/authJWTtoken/**").permitAll()
	            	.antMatchers("/swagger-ui.html**").permitAll()
	                .antMatchers("/swagger-resources/**").permitAll()
                    .antMatchers("/configuration/**").permitAll()
                    .antMatchers("/v2/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/**").permitAll();
                    
                    //.antMatchers("/oficinaCepat/registro/**").permitAll()
                    
	            	// esto se debe activar en cuanto se suba a produccion
	              //.anyRequest().authenticated();
                    
             http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
            // disable page caching
            http.headers().cacheControl();
        }
    }
    

	
}


