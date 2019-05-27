package issstep.afiliacion.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;



public class Usuario implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;

	
	public Usuario() {
	}

	
	@JsonIgnore
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;//getUsuario();
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;//this.getPasswd();
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		
			return false;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@JsonIgnore
	public Date getLastPasswordResetDate() {
		return new Date();
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
			return true;
		
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	/*public Collection<? extends SimpleGrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		System.out.println(getRol().getClave());
		authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(getRol().getClave()));

		return authorities;
	}

	public void setUserAuthorities(List<String> roles) {
		for (String role : roles)
			authorities.add(new SimpleGrantedAuthority(role));
	}*/

}