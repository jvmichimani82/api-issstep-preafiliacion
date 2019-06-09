package issstep.afiliacion.model;

import java.sql.Timestamp;

public class Archivo {

	long id;
	long tipoDocto;
	String urlDocto;
	String nombreDocto;
	Timestamp fechaRegistro;
	int estatus;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTipoDocto() {
		return tipoDocto;
	}
	public void setTipoDocto(long tipoDocto) {
		this.tipoDocto = tipoDocto;
	}
	public String getUrlDocto() {
		return urlDocto;
	}
	public void setUrlDocto(String urlDocto) {
		this.urlDocto = urlDocto;
	}
	public String getNombreDocto() {
		return nombreDocto;
	}
	public void setNombreDocto(String nombreDocto) {
		this.nombreDocto = nombreDocto;
	}
	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public int getEstatus() {
		return estatus;
	}
	public void setEstatus(int estatus) {
		this.estatus = estatus;
	}
	
	
	
}
