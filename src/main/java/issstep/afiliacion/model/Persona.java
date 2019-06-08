package issstep.afiliacion.model;

import java.sql.Timestamp;

public class Persona {

	long id;
	String curp;
	boolean renapoValidacion;
	String apellidoPaterno;
	String apellidoMaterno;
	String nombre;
	String sexo;
	String email;
	Timestamp fechaNacimiento;
	String nacionalidad;
	String documentoProbatorio;
	long entidad;
	String entitadDes;
	long municipio;
	String municipioDesc;
	String rfc;
	boolean satValidacion;
	Timestamp fechaRegistro;
	Timestamp ultimaModificacion;
	int estatus;

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

}
