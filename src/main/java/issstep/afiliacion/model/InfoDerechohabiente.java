package issstep.afiliacion.model;

import java.sql.Timestamp;

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
	
	long noControlTitular;
	
	long noPreAfiliacionTitular;
	
	String nombre;
	
	String paterno;
	
	String materno;
	
	String curp;
	
	String email;
	
	long claveUsuarioRegistro;
	
	String nombreCompleto;
	
	long noAfiliacion;
	
	long situacion;
	
	long estatus;
	
	long claveParentesco;
	
	String parentesco;
	
	Timestamp fechaAfiliacion;
	
	Timestamp fechaVerificacion;
	
	public String getNombreCompleto() {
		return this.nombre.trim() + " " + this.paterno.trim() + " " + this.materno.trim();	
	}
	
	public long getNoControlTitular() {
		return this.noControl;
	}
	
	public long getNoPreAfiliacionTitular() {
		return this.noControl;
	}
	
	public String getParentesco() {
		if(this.claveParentesco == 0)
			return "TITULAR";
		return "BENEFICIARIO";
	}
	
	public long getEstatus() {
		if(this.noAfiliacion > 0)
			return 4l;
		else
			return this.estatus;
	}
	
}
