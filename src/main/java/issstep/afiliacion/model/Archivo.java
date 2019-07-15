package issstep.afiliacion.model;

import java.sql.Timestamp;

public class Archivo {

	long noTrabajador;
	long noBeneficiario;
	long noParentesco;
	long noTArchivo;
	String nombre;
	String urlArchivo;
	long valido ;
	Timestamp fechaRegistro;
	int activo;
	
	public long getNoTrabajador() {
		return noTrabajador;
	}
	
	public void setNoTrabajador(long noTrabajador) {
		this.noTrabajador = noTrabajador;
	}
	
	public long getNoBeneficiario() {
		return noBeneficiario;
	}
	
	public void setNoBeneficiario(long noBeneficiario) {
		this.noBeneficiario = noBeneficiario;
	}
	
	public long getNoParentesco() {
		return noParentesco;
	}
	
	public void setNoParentesco(long noParentesco) {
		this.noParentesco = noParentesco;
	}
	
	public long getNoTArchivo() {
		return noTArchivo;
	}
	public void setNoTArchivo(long noTArchivo) {
		this.noTArchivo = noTArchivo;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getUrlArchivo() {
		return urlArchivo;
	}
	
	public void setUrlArchivo(String urlArchivo) {
		this.urlArchivo = urlArchivo;
	}
	
	public long getValido() {
		return valido;
	}
	
	public void setValido(long valido) {
		this.valido = valido;
	}
	
	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}
	
	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public int getActivo() {
		return activo;
	}
	
	public void setActivo(int activo) {
		this.activo = activo;
	}	
	
}
