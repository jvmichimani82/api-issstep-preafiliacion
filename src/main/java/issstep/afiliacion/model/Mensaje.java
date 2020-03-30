package issstep.afiliacion.model;

import com.fasterxml.jackson.annotation.JsonView;

public class Mensaje {
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroDerechohabiente.class})
	String mensaje;
	
	public Mensaje() {
		
	}
	
	public Mensaje (String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	public static final class Views {
		public interface Simple {}
		public interface RegistroDerechohabiente {}
	
	}

}
