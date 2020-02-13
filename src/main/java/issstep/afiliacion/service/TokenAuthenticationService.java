package issstep.afiliacion.service;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.model.Usuario;



@Service
public class TokenAuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);	
	
	@Autowired
	UsuarioDB usuarioDB;
		
	public Authentication getAuthentication(String nombreUsuario, String password) {
		Usuario userLogin = usuarioDB.getSession(nombreUsuario, Hashing.sha256().hashString(password, Charsets.UTF_8).toString());
	
		 System.out.println("Autent...."+ Hashing.sha256().hashString(password, Charsets.UTF_8).toString());
		
		if (userLogin!=null)
			return new UsernamePasswordAuthenticationToken(userLogin, password, userLogin.getAuthorities());
		
		else
			return null;
		
    }
}
