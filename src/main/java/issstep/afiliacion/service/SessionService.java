package issstep.afiliacion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SessionService implements UserDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);	
	
	
	@Override
	public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
		return null;//getUsuario(arg0);
	}
	
	/*@SuppressWarnings("unused")
	@Transactional
	private Usuario getUsuario(String nombreUsuario) throws UsernameNotFoundException {
		Usuario userLogin = usuarioRepository.findUsuarioByUsuario(nombreUsuario);
		if(userLogin != null)
			return userLogin;
		else 
			throw new UsernameNotFoundException("Username not found.");			
	}*/

}