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
public class Derechohabiente {

	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroUsuario.class})
	long noControl;
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroUsuario.class})
	long noPreAfiliacion;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String nombre;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String paterno;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String materno;
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroUsuario.class})
	String email;
		
	@JsonView({Derechohabiente.Views.Simple.class})
	Timestamp fechaNacimiento;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String sexo;	
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroUsuario.class})
	String curp;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String rfc;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String domicilio;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String codigoPostal;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String telefonoCasa;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String telefonoCelular;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	Timestamp fechaPreAfiliacion;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long situacion;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveUsuarioRegistro;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	Timestamp fechaRegistro;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveUsuarioModificacion;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	Timestamp fechaModificacion;
		
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveEstado;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String estado;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveMunicipio;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String municipio;

	@JsonView({Derechohabiente.Views.Simple.class})
	long claveLocalidad;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String localidad;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveColonia;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String colonia;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveClinicaServicio;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String clinicaServicio;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long claveEstadoCivil;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String estadoCivil;
	
	@JsonView({Derechohabiente.Views.Simple.class})	
	String nombreCompleto;


	public String getNombreCompleto() {
		return this.nombre +" "+ this.paterno+" "+ this.materno;	
	}
	
	public static final class Views {
		public interface Simple {}
		public interface RegistroUsuario extends Usuario.Views.RegistroUsuario {}
	}

}
