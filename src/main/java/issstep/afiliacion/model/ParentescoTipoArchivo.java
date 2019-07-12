package issstep.afiliacion.model;

public class ParentescoTipoArchivo {
	private long noTArchivo;
	private long obligatorio;
	private String archivo;
	
	public long getNoTArchivo() {
		return noTArchivo;
	}
	
	public void setNoTArchivo(long noTArchivo) {
		this.noTArchivo = noTArchivo;
	}
	
	public long getObligatorio() {
		return obligatorio;
	}
	
	public void setObligatorio(long obligatorio) {
		this.obligatorio = obligatorio;
	}
	public String getArchivo() {
		return archivo;
	}
	
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

}
