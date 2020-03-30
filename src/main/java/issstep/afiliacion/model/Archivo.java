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
public class Archivo {
	@JsonView({Archivo.Views.Simple.class})
	long claveDocumento;
	
	@JsonView({Archivo.Views.Simple.class})
	long noControlTitular;
	
	@JsonView({Archivo.Views.Simple.class})
	long noControl;
	
	@JsonView({Archivo.Views.Simple.class})
	long noPreAfiliacion;
	
	@JsonView({Archivo.Views.Simple.class})
	long noBeneficiario;
	
	@JsonView({Archivo.Views.Simple.class})
	long claveParentesco;
	
	@JsonView({Archivo.Views.Simple.class})
	long claveTipoArchivo;
	
	@JsonView({Archivo.Views.Simple.class})
	String nombre;
	
	@JsonView({Archivo.Views.Simple.class})
	String comentario;
	
	@JsonView({Archivo.Views.Simple.class})
	String urlArchivo;
	
	@JsonView({Archivo.Views.Simple.class})
	int esValido ;
	
	@JsonView({Archivo.Views.Simple.class})
	long claveUsuarioRegistro;
	
	@JsonView({Archivo.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Archivo.Views.Simple.class})
	long claveUsuarioModificacion;
	
	@JsonView({Archivo.Views.Simple.class})
	int estatus ;
	
	public static final class Views {
		public interface Simple {}
	}
}
