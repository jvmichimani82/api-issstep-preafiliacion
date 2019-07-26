package issstep.afiliacion.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);	

    @Autowired  
    private JwtTokenUtil jwtTokenUtil;

    public MessageUtils messageUtils = new MessageUtils();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getParameter(messageUtils.getMessage("header_jwt_token")) == null ? httpRequest.getHeader(messageUtils.getMessage("header_jwt_token")) : httpRequest.getParameter(messageUtils.getMessage("header_jwt_token")) ;
        System.out.println(authToken);
		
         	String username = jwtTokenUtil.getUsernameFromToken(authToken);
         	System.out.println(username);
   
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        	List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
	    		authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
	    		//System.out.println(jwtTokenUtil.getRolFromToken(authToken));
	    		
	    		if(jwtTokenUtil.getRolFromToken(authToken) != "")
	    		authorities.add(new SimpleGrantedAuthority(jwtTokenUtil.getRolFromToken(authToken)));
	        	
	             if (jwtTokenUtil.validateToken(authToken, null)) {
	                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }
     
        chain.doFilter(request, response);
    }
    
}