package issstep.afiliacion.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonView;

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
public class Colonia {
	long claveColonia;
	
	String descripcion;
	
	long codigoPostal;
	
	long claveClinicaServicio;
	
	long claveEstado;
	
	long claveMunicipio;
	
	long claveLocalidad;
}
