package issstep.afiliacion.model;

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
public class ResultadoBusqueda {

	long noControl;
	
	long noAfiliacion;
	
	long noPreAfiliacion;
	
	long noBeneficiario;
	
	String nombre;
	
	String paterno;
	
	String materno;
	
	String sexo;	
	
	String curp;
	
	long claveParentesco;
	
	String parentesco;
	
	String nombreCompleto;
	
	public String getNombreCompleto() {
		return this.nombre.trim() + " " + this.paterno.trim() + " " + this.materno.trim();	
	}
	

}
