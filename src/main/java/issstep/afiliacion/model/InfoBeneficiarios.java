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
public class InfoBeneficiarios {
	long noControlTitular;
	
	long noPreAfiliacionTitular;
	
	String nombreTitular;
	
	String emailTitular;
	
	long noControlBeneficiario;
	
	long noAfiliacionBeneficiario;
	
	long noPreAfiliacionBeneficiario;
	
	String nombreBeneficiario;
	
}
