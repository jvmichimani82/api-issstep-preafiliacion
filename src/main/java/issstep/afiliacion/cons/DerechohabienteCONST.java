package issstep.afiliacion.cons;


public class DerechohabienteCONST {
	
	
	
	public static final String registroUsuario="Json para Registro Usuario online \n\n"+"{"
			+"\r\n" + 
			"			    \"nocontrol\": \"100\",\r\n" +
			"			    \"noAfiliacion\": \"100\",\r\n" + 
			"			    \"curp\": \"RHUJ820830HPLLCS08\",\r\n" + 
			"			    \"email\": \"email@correo.com\",\r\n" + 
			"			    \"usuario\": {\r\n" + 
			"			      \"login\": \"email@correo.com\",\r\n" + 
			"			      \"passwd\": \"12345678\"\r\n" + 
			"			    },\r\n" + 
		
		
			"			  }";
	
	public static final String registroDerechohabiente="Json para Registro de derechohabiente\n\n"+"{"
			+"\n" + 
			"			    \"noControl\": 20011,\n" + 
			"			    \"noPreAfiliacion\": 20011,\n" + 
			"			    \"nombre\": \"JOSE LUIS\",\n" + 
			"			    \"paterno\": \"OSORIO\",\n" + 
			"			    \"materno\": \"CAMPOS\",\n" + 
			"			    \"email\": \"email@correo.com\",\n" + 
			"			    \"fechaNacimiento\": \"2000-01-01\",\n" +
			"			    \"sexo\": \"M\",\n" + 
			"			    \"curp\": \"RHUJ820830HPLLCS08\",\n" + 
			"			    \"rfc\": \"RHUJ820830S08\",\n" +
			"			    \"domicilio\": \"direccion\",\n" + 
			"			    \"codigoPostal\": 72755,\n" + 
			"			    \"telefonoCasa\": \"1234567890\",\n" + 
			"			    \"telefonoCelular\": \"1234567890\",\n" + 
			"			    \"claveEstadoCivil\": 2,\n" + 
			"			    \"claveColonia\": 366,\n" + 
			"			    \"claveClinicaServicio\": 19,\n" + 
			"			    \"claveLocalidad\": 1,\n" +
			"			    \"claveMunicipio\": 114,\n" +
			"			    \"claveEstado\": 21\n" +
			
			"			    }" ;
	
	public static final String buscaDerechohabiente="Json para Busqueda de derechohabiente\n\n"+"{"
			+"\n" + 
			"			    \"nombre\": \"JULIAN CAMILO AURELIO\",\n" + 
			"			    \"paterno\": \"ABAD\",\n" + 
			"			    \"materno\": \"CAMACHO\",\n" + 
			"			    \"fechaNacimiento\": \"-544903200000\",\n" +
			"			    \"sexo\": \"M\",\n" + 
			"			    \"claveEstado\": 21\n" +
			
			"			    }" ;
	
	public static final String asignaBeneficiario="Json para Asignar beneficiario a derechohabiente\n\n"+"{"
			+"\n" + 
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
	
	public static final String curp ="{\"curp\":\"VAMJ820830HPLLCS07\"}";
}
