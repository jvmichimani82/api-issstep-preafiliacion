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

	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	long id;
	
	long rol;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	String login;
	
	String passwd;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	String token;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	Timestamp fechaRegistro;
	
	@JsonView(Usuario.Views.Simple.class)
	Timestamp ultimaModificacion;
	
	@JsonView({Usuario.Views.Simple.class, Usuario.Views.RegistroUsuario.class})
	int estatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String nombre) {
		this.login = nombre;
	}

	public long getRol() {
		return rol;
	}

	public void setRol(long rol) {
		this.rol = rol;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Timestamp getUltimaModificacion() {
		return ultimaModificacion;
	}

	public void setUltimaModificacion(Timestamp ultimaModificacion) {
		this.ultimaModificacion = ultimaModificacion;
	}

	public int getEstatus() {
		return estatus;
	}

	public void setEstatus(int estatus) {
		this.estatus = estatus;
	}

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