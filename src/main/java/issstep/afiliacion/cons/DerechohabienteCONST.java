package issstep.afiliacion.cons;


public class DerechohabienteCONST {
	
	
	
	public static final String registroUsuario="Json para Registro Usuario online \n\n"+"{"
			+"\r\n" + 
			"			    \"curp\": \"AACJ520925HPLBML03\",\r\n" + 
			"			    \"email\": \"email@correo.com\",\r\n" + 
			"			    \"usuario\": {\r\n" + 
			"			      \"login\": \"email@correo.com\",\r\n" + 
			"			      \"passwd\": \"21531apps\"\r\n" + 
			"			    }\r\n" + 	
			"			  }";
	
	public static final String registroDerechohabiente="Json para Registro de derechohabiente Titular\n\n"+"{"
			+"\n" + 
			"			    \"curp\": \"RUHG820830HPLLCS08\",\n" + 
			"			    \"rfc\": \"RUHG820830S08\",\n" +
			"			    \"direccion\": \"direccion\",\n" + 
			"			    \"telefonoCasa\": \"1234567890\",\n" + 
			"			    \"telefonoCelular\": \"1234567890\",\n" + 
			"			    \"claveEstadoCivil\": 2,\n" + 
			"			    \"claveParentesco\": 3,\n" +
			"			    \"claveEstado\": 21,\n" +
			"			    \"claveMunicipio\": 114,\n" +
			"			    \"claveLocalidad\": 1,\n" +
			"			    \"claveColonia\": 24,\n" +
			"			    \"codigoPostal\": 72014,\n" +
			"			    \"claveClinicaServicio\": 20,\n" +
			"			    \"fechaPreAfiliacion\": \"2019-01-01\"\n" +
			"			    } \n\n\n" +
			"Json para Registro de derechohabiente Beneficiario\n\n"+"{"
			+"\n" + 
			"			    \"curp\": \"RUHG820830HPLLCS08\",\n" + 
			"			    \"rfc\": \"RUHG820830S08\",\n" +
			"			    \"claveEstadoCivil\": 2,\n" + 
			"			    \"claveParentesco\": 3\n" +
			"			    }";
	
	public static final String buscaDerechohabiente="Json para Busqueda de derechohabiente\n\n"+"{"
			+"\n" + 
			"			    \"nombre\": \"JULIAN CAMILO AURELIO\",\n" + 
			"			    \"paterno\": \"ABAD\",\n" + 
			"			    \"materno\": \"CAMACHO\",\n" + 
			"			    \"fechaNacimiento\": \"-544903200000\",\n" +
			"			    \"sexo\": \"M\",\n" + 
			"			    \"claveEstado\": 21\n" +
			
			"			    }" ;
	
	public static final String informacionBeneficiario="Json para Asignar beneficiario a derechohabiente\n\n"+"{"
			+"\n" + 
			"			    \"noControlTitular\": 200,\n" + 	
			"			    \"noControl\": 20011,\n" + 
			"			    \"noPreAfiliacion\": 20011,\n" +
			"			    \"claveParentesco\": 1\n" +
			"			    }" ;
	
	public static final String recuperarPassword="Json para Recuperar password\n\n"+"{"
			+"\n" + 
			"			    \"token\": \"5c7096378ade17e9cf5b53ac6490d0e431ea5a99e6d01ba81f257f03a95b70ef\",\n" + 
			"			    \"password\": \"nuevoPassword\"\n" +
			"			    }" ;
	
	public static final String actualizarPassword="Json para Actualizar password\n\n"+"{"
			+"\n" + 
			"			    \"passwordActual\": \"PasswordActual\",\n" + 
			"			    \"passwordNuevo\": \"PasswordNuevo\"\n" +	
			"			    }" ;
	
	public static final String actualizarDatos="Json para Actualizar direccion\n\n"+"{"
			+"\n" + 
			"			    \"noControl\": 20011,\n" + 
			"			    \"noPreAfiliacion\": 20011,\n" + 
			"			    \"direccion\": \"nueva direccion\",\n" +
			"			    \"telefonoCasa\": \"1234567890\",\n" + 
			"			    \"telefonoCelular\": \"1234567890\"\n" + 
			"			    }" ;

	public static final String email ="{\"email\":\"email@correo.com\"}";
	
	public static final String curp ="{\"rfc\":\"VAMJ820830\"}";
}
