package issstep.afiliacion.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	long claveUsuario;
	
	long claveRol;
	
	long noControl;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	String login;
	
	String passwd;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	String token;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	Timestamp fechaRegistro;
	
	@JsonView(Usuario.Views.Simple.class)
	Timestamp fechaUltimoAcceso;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	int estatus;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	long noAfiliacion;

	@JsonIgnore
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;// getUsuario();
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;// this.getPasswd();
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
	
	
	public static final class Views {
		public interface Simple {}
		public interface RegistroUsuario{}
	}

	/*
	 * public Collection<? extends SimpleGrantedAuthority> getAuthorities() { //
	 * TODO Auto-generated method stub System.out.println(getRol().getClave());
	 * authorities = new ArrayList<SimpleGrantedAuthority>(); authorities.add(new
	 * SimpleGrantedAuthority(getRol().getClave()));
	 * 
	 * return authorities; }
	 * 
	 * public void setUserAuthorities(List<String> roles) { for (String role :
	 * roles) authorities.add(new SimpleGrantedAuthority(role)); }
	 */

}