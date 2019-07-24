package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.security.JwtAuthenticationRequest;
import issstep.afiliacion.security.JwtAuthenticationResponse;
import issstep.afiliacion.security.JwtTokenUtil;
import issstep.afiliacion.service.TokenAuthenticationService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationRestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;
    
    
    @Autowired
    public MessageSource messageSource;
    
    @RequestMapping(value = "/authJWTtoken", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device, ServletResponse response) throws AuthenticationException {
    	Authentication authentication = tokenAuthenticationService.getAuthentication(authenticationRequest.getUsername(),authenticationRequest.getPassword());
    	if(authentication != null) {
	    	SecurityContextHolder.getContext().setAuthentication(authentication);
	        Usuario usuarioDetails = (Usuario) authentication.getPrincipal();
	        System.out.println("Usuario ==> ");
	        System.out.println(usuarioDetails.getNoControl());
	        System.out.println(usuarioDetails.getEstatus());
	        
	        if (usuarioDetails.getEstatus() == -1 )
	        	return new ResponseEntity<>(new Mensaje("Usuario no activo"), HttpStatus.CONFLICT);
	        
	        if (usuarioDetails.getEstatus() == 0 )
	        	return new ResponseEntity<>(new Mensaje("Usuario no desactivado"), HttpStatus.CONFLICT);
	        
   	        String token = jwtTokenUtil.generateToken(usuarioDetails, device, usuarioDetails);
		     
		    return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	    
    	}
    	return new ResponseEntity<>(new Mensaje("No existe el usuario"), HttpStatus.BAD_REQUEST);
    }
}