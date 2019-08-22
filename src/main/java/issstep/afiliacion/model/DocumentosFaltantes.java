package issstep.afiliacion.model;

import java.sql.Date;

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
public class DocumentosFaltantes {
	
	long noControl;
	
	long noPreAfiliacion;
	
	long claveParentesco;
	 
	long claveTipoArchivo;
	
	Date fechaNacimiento;
	
	int esObligatorio;
	
	int estatus;

}
