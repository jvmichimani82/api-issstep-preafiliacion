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
public class Clinica {
	
	long claveClinicaServicio;
	
	long nivel;
	
	String descripcion;	
	
	long regionIssstep;
}
