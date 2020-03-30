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
public class InfoDerechohabiente {
	long noControl;
	
	long noPreAfiliacion;
	
	String nombre;
	
	String paterno;
	
	String materno;
	
	String curp;
	
	long claveUsuarioRegistro;
	
	String nombreCompleto;
	
	long noAfiliacion;
	
	public String getNombreCompleto() {
		return this.nombre.trim() + " " + this.paterno.trim() + " " + this.materno.trim();	
	}
	
}
