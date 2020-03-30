package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.security.JwtAuthenticationRequest;
import issstep.afiliacion.security.JwtAuthenticationResponse;
import issstep.afiliacion.security.JwtTokenUtil;
import issstep.afiliacion.service.DerechohabienteService;
import issstep.afiliacion.service.TokenAuthenticationService;

import javax.servlet.ServletResponse;

@RestController
public class AuthenticationRestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;
    
    
    @Autowired
    public MessageSource messageSource;
    
    @Autowired
    public DerechohabienteService derechohabienteService;
    
    
    @RequestMapping(value = "/authJWTtoken", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device, ServletResponse response) throws AuthenticationException {
    	Authentication authentication = tokenAuthenticationService.getAuthentication(authenticationRequest.getUsername(),authenticationRequest.getPassword());
    	System.out.println(authentication);
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
    
    @RequestMapping(value = "/confirmar-email/activar/{token}", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activarRegistro(@ApiParam(value = "Token", required = true) @PathVariable String token) {

    	return derechohabienteService.activarRegistro(token);
	}
    
}