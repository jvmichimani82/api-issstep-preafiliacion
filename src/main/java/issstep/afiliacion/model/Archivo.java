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
	long noTrabajador;
	
	@JsonView({Archivo.Views.Simple.class})
	long noBeneficiario;
	
	@JsonView({Archivo.Views.Simple.class})
	long noParentesco;
	
	@JsonView({Archivo.Views.Simple.class})
	long noTArchivo;
	
	@JsonView({Archivo.Views.Simple.class})
	String nombre;
	
	@JsonView({Archivo.Views.Simple.class})
	String urlArchivo;
	
	@JsonView({Archivo.Views.Simple.class})
	int validado ;
	
	@JsonView({Archivo.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Archivo.Views.Simple.class})
	int activo;
	
	public static final class Views {
		public interface Simple {}
	}
}
