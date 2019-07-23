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
public class Beneficiario {
	@JsonView({Beneficiario.Views.Simple.class})
	long noBeneficiario;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long noControl;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long noPreAfiliacion;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long claveParentesco;
	
	@JsonView({Beneficiario.Views.Simple.class})
	Timestamp fechaAfiliacion;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long situacion;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long claveUsuarioRegistro;
	
	@JsonView({Beneficiario.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Beneficiario.Views.Simple.class})
	long claveUsuarioModificacion;
	
	
	public static final class Views {
		public interface Simple {}
	}

}
