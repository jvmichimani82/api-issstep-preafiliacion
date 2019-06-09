package issstep.afiliacion.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonView;

public class Persona {

	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	long id;
	
	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	String curp;
	
	@JsonView({Persona.Views.Simple.class})
	boolean renapoValidacion;
	
	@JsonView({Persona.Views.Simple.class})
	String apellidoPaterno;
	
	@JsonView({Persona.Views.Simple.class})
	String apellidoMaterno;
	
	@JsonView({Persona.Views.Simple.class})
	String nombre;
	
	@JsonView({Persona.Views.Simple.class})
	String sexo;
	
	@JsonView({Persona.Views.Simple.class, Persona.Views.RegistroUsuario.class})
	String email;
	
	@JsonView({Persona.Views.Simple.class})
	Timestamp fechaNacimiento;
	
	@JsonView({Persona.Views.Simple.class})
	String nacionalidad;
	
	@JsonView({Persona.Views.Simple.class})
	String documentoProbatorio;
	
	@JsonView({Persona.Views.Simple.class})
	long entidad;
	
	@JsonView({Persona.Views.Simple.class})
	String entitadDes;
	
	@JsonView({Persona.Views.Simple.class})
	long municipio;
	
	@JsonView({Persona.Views.Simple.class})
	String municipioDesc;
	
	@JsonView({Persona.Views.Simple.class})
	String rfc;
	
	@JsonView({Persona.Views.Simple.class})
	boolean satValidacion;
	
	@JsonView({Persona.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Persona.Views.Simple.class})
	Timestamp ultimaModificacion;
	
	@JsonView({Persona.Views.Simple.class})
	int estatus;
	
	@JsonView({Persona.Views.RegistroUsuario.class})
	Usuario usuario;
	
	String nombreCompleto;

	public Persona() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCurp() {
		return curp;
	}

	public void setCurp(String curp) {
		this.curp = curp;
	}

	public boolean isRenapoValidacion() {
		return renapoValidacion;
	}

	public void setRenapoValidacion(boolean renapoValidacion) {
		this.renapoValidacion = renapoValidacion;
	}

	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Timestamp getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Timestamp fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getDocumentoProbatorio() {
		return documentoProbatorio;
	}

	public void setDocumentoProbatorio(String documentoProbatorio) {
		this.documentoProbatorio = documentoProbatorio;
	}

	public long getEntidad() {
		return entidad;
	}

	public void setEntidad(long entidad) {
		this.entidad = entidad;
	}

	public String getEntitadDes() {
		return entitadDes;
	}

	public void setEntitadDes(String entitadDes) {
		this.entitadDes = entitadDes;
	}

	public long getMunicipio() {
		return municipio;
	}

	public void setMunicipio(long municipio) {
		this.municipio = municipio;
	}

	public String getMunicipioDesc() {
		return municipioDesc;
	}

	public void setMunicipioDesc(String municipioDesc) {
		this.municipioDesc = municipioDesc;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public boolean isSatValidacion() {
		return satValidacion;
	}

	public void setSatValidacion(boolean satValidacion) {
		this.satValidacion = satValidacion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNombreCompleto() {
		return this.nombre +" "+ this.apellidoPaterno+" "+ this.apellidoMaterno;
	
	}

	public static final class Views {
		public interface Simple {}
		public interface RegistroUsuario extends Usuario.Views.RegistroUsuario {}
	}

}
