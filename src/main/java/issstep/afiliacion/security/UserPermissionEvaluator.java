package issstep.afiliacion.security;

import java.io.Serializable;
import java.security.Permission;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import issstep.afiliacion.service.SessionService;

public class UserPermissionEvaluator implements PermissionEvaluator {
    
	private SessionService service;

    @Autowired
    public UserPermissionEvaluator(SessionService service) {
        this.service = service;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

    	if(!authentication.getPrincipal().equals("anonymousUser")) {
	        String principal = (String) authentication.getPrincipal();
	        //Usuario userDetails = (Usuario) service.loadUserByUsername(principal);
	        //Collection<GrantedAuthority> userPermissions = userDetails.getPermissions(authentication.getAuthorities());
	        
	        /*for (GrantedAuthority permiso : userPermissions) {
	            if (permiso.getAuthority().equals(permission)) {
	                return true;
	            }
	        }*/
    	}
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,  String targetType, Object permission) {
        throw new RuntimeException("Error");
    }
}