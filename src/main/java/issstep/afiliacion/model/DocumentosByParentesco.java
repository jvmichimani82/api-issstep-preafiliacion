package issstep.afiliacion.model;

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
public class DocumentosByParentesco {
	@JsonView({DocumentosByParentesco.Views.Simple.class})
	long claveParentesco;
	
	@JsonView({DocumentosByParentesco.Views.Simple.class})
	long claveTipoArchivo;
	
	@JsonView({DocumentosByParentesco.Views.Simple.class})
	long esObligatorio;
	
	@JsonView({DocumentosByParentesco.Views.Simple.class})
	String archivo;
	
	public static final class Views {
		public interface Simple {}
	}

}
