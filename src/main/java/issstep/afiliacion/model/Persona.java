package issstep.afiliacion.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonView;

public class Persona {

	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	long noControl;
	
	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	long noAfiliacion;
	
	@JsonView({Persona.Views.Simple.class})
	String nombre;
	
	@JsonView({Persona.Views.Simple.class})
	String paterno;
	
	@JsonView({Persona.Views.Simple.class})
	String materno;
	
	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	String email;
		
	@JsonView({Persona.Views.Simple.class})
	Timestamp fechaNacimiento;
	
	@JsonView({Persona.Views.Simple.class})
	String sexo;	
	
	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	String curp;
	
	@JsonView({Persona.Views.Simple.class})
	String rfc;
	
	@JsonView({Persona.Views.Simple.class})
	String domicilio;
	
	@JsonView({Persona.Views.Simple.class})
	String codigoPostal;
	
	@JsonView({Persona.Views.Simple.class})
	String telefono;
	
	@JsonView({Persona.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Persona.Views.Simple.class})
	Timestamp fechaModificacion;
	
	@JsonView({Persona.Views.Simple.class})
	long situacion;
		
	@JsonView({Persona.Views.Simple.class})
	long noColonia;
	
	@JsonView({Persona.Views.Simple.class})
	long noEntidad;
	
	@JsonView({Persona.Views.Simple.class})
	String entidad;
	
	@JsonView({Persona.Views.Simple.class})
	long noMunicipio;
	
	@JsonView({Persona.Views.Simple.class})
	String municipio;

	@JsonView({Persona.Views.Simple.class})
	long noLocalidad;
	
	@JsonView({Persona.Views.Simple.class})
	String localidad;
	
	@JsonView({Persona.Views.RegistroUsuario.class})
	Usuario usuario;
	
	String nombreCompleto;

	public Persona() {
	}

	public long getNoControl() {
		return noControl;
	}

	public void setNoControl(long noControl) {
		this.noControl = noControl;
	}

	public long getNoAfiliacion() {
		return noAfiliacion;
	}

	public void setNoAfiliacion(long noAfiliacion) {
		this.noAfiliacion = noAfiliacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPaterno() {
		return paterno;
	}

	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	public String getMaterno() {
		return materno;
	}

	public void setMaterno(String materno) {
		this.materno = materno;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Timestamp fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getCurp() {
		return curp;
	}

	public void setCurp(String curp) {
		this.curp = curp;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Timestamp getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Timestamp fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public long getSituacion() {
		return situacion;
	}

	public void setSituacion(long situacion) {
		this.situacion = situacion;
	}

	public long getNoColonia() {
		return noColonia;
	}

	public void setNoColonia(long noColonia) {
		this.noColonia = noColonia;
	}

	public long getNoEntidad() {
		return noEntidad;
	}

	public void setNoEntidad(long noEntidad) {
		this.noEntidad = noEntidad;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public long getNoMunicipio() {
		return noMunicipio;
	}

	public void setNoMunicipio(long noMunicipio) {
		this.noMunicipio = noMunicipio;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public long getNoLocalidad() {
		return noLocalidad;
	}

	public void setNoLocalidad(long noLocalidad) {
		this.noLocalidad = noLocalidad;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getNombreCompleto() {
		return this.nombre +" "+ this.paterno+" "+ this.materno;	
	}

	public static final class Views {
		public interface Simple {}
		public interface RegistroUsuario extends Usuario.Views.RegistroUsuario {}
	}

}
