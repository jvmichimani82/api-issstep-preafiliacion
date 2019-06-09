package issstep.afiliacion.model;

public class Documento  {
	
	private long id;
	private String descripcion;
	
	public Documento() {
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}