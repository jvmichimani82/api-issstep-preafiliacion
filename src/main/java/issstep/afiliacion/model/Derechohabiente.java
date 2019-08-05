package issstep.afiliacion.model;

import java.sql.Date;
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

	@JsonView({Derechohabiente.Views.Simple.class})
	long noControl;
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroDerechohabiente.class})
	long noPreAfiliacion;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	long noBeneficiario;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String nombre;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String paterno;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String materno;
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroDerechohabiente.class})
	String email;
		
	@JsonView({Derechohabiente.Views.Simple.class})
	Date fechaNacimiento;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String sexo;	
	
	@JsonView({Derechohabiente.Views.Simple.class, Derechohabiente.Views.RegistroDerechohabiente.class})
	String curp;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String rfc;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String direccion;
	
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
	long claveParentesco;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String parentesco;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	int estatus;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	String estatusDescripcion;
	
	@JsonView({Derechohabiente.Views.Simple.class})	
	String nombreCompleto;
	
	@JsonView({Derechohabiente.Views.Simple.class})
	Usuario usuario;
	
	


	public String getNombreCompleto() {
		return this.nombre.trim() + " " + this.paterno.trim() + " " + this.materno.trim();	
	}
	
	public static final class Views {
		public interface Simple extends Mensaje.Views.Simple {}
		public interface RegistroDerechohabiente {}
	}

}
