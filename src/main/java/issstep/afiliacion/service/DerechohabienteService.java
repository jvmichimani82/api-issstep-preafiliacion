package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

// import org.json.JSONObject;
/* import org.slf4j.Logger;
import org.slf4j.LoggerFactory; */
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import issstep.afiliacion.db.DerechohabienteDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.db.BeneficiarioDB;
import issstep.afiliacion.db.CatalogoGenericoDB;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.NumerosParaRegistro;
import issstep.afiliacion.model.ResetPassword;
import issstep.afiliacion.model.ResultadoBusqueda;
import issstep.afiliacion.model.ResultadoCreacionCuenta;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.DocumentosFaltantes;
import issstep.afiliacion.model.InfoDerechohabiente;
import issstep.afiliacion.model.InfoPersona;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.DatoABuscar;
import issstep.afiliacion.model.ResultadoValidacion;
import issstep.afiliacion.utils.Utils;

@Service
public class DerechohabienteService {
	// private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
	@Qualifier("")
	private static final int HIJO = 6;
	private static final int HIJA = 7;
	
	private static final int CONST_ESTUDIOS = 5;
	
	private static final int LIMITE_EDAD = 18;
	private static final int INACTIVA = -1;
		
	@Autowired
	DerechohabienteDB personaDB;
	
	@Autowired
	UsuarioDB usuarioDB;
	
	@Autowired
	BeneficiarioDB beneficiarioDB;
	
	@Autowired
	CatalogoGenericoDB catalogoGenericoDB;
	
	@Autowired
	MailService mailService;
		
	public ResponseEntity<?> getPersonaByCurp(String curp) {
		if (curp == null)
			return new ResponseEntity<>(new Mensaje("Debe proporcionar el campo curp"), HttpStatus.BAD_REQUEST);
		
		if (!Utils.esCURP(curp))
			return new ResponseEntity<>(new Mensaje("Formato de curp invalido"), HttpStatus.BAD_REQUEST);
		
		// Revisamos que la persona exista en nuestra base de datos
		Derechohabiente persona =  personaDB.getPersonaByColumnaStringValor("CURP", curp);
			
		// Sino tenemos resultado de nuestra base de datos vamos por los datos a la bd de issstep
		if(persona == null) 
			persona = personaDB.getTrabajadorIssstepByColumnaStringValor("CURP", curp);
			
		if (persona == null)
			return new ResponseEntity<>(new Mensaje("No existe el derechohabiente"), HttpStatus.NOT_FOUND);
		
		fillDerechohabiente(persona);
		return new ResponseEntity<>(persona, HttpStatus.OK);		
    }
	
	public ResponseEntity<?> validaPersonaNoAfiliacion(long noAfiliacion) {
		InfoPersona infoPersona = creaInforPersona(noAfiliacion, noAfiliacion, noAfiliacion, 0);
		
		ResponseEntity<?> persona = getPersonaByNoControlNoPreafiliacion(infoPersona);
		
		if (persona.getStatusCodeValue() == 200) {
			// fillDerechohabiente(persona);
			return persona;
		}
			//return new ResponseEntity<>(new Mensaje("Ya existe una cuenta con ese numero de afiliacion"), HttpStatus.CONFLICT);
			
		return getAfiliadoByNoControlNoPreafiliacion(noAfiliacion, noAfiliacion, 0);			
	}
	
	public ResponseEntity<?> getPersonaByNombre(Derechohabiente persona, HttpServletResponse response) {
		Derechohabiente personaOld =  personaDB.getPersonaByNombre(persona, response);
				
		if (personaOld != null) {
			fillDerechohabiente(personaOld);
			return new ResponseEntity<>(personaOld, HttpStatus.OK);
		}
		else {
			if(response.getStatus() == 429)
				return new ResponseEntity<>(new Mensaje("Multiples resultados para la busqueda"), HttpStatus.CONFLICT);
			else
				return new ResponseEntity<>(new Mensaje("No existe persona con esos datos"), HttpStatus.NOT_FOUND);	
		}
		
    }
	
	void fillDerechohabiente(Derechohabiente derechohabiente){
		
		derechohabiente.setEstado(catalogoGenericoDB.getDescripcionCatalogo("KESTADO", derechohabiente));
		derechohabiente.setMunicipio(catalogoGenericoDB.getDescripcionCatalogo("KMUNICIPIO", derechohabiente));
		derechohabiente.setLocalidad(catalogoGenericoDB.getDescripcionCatalogo("KLOCALIDAD", derechohabiente));
		derechohabiente.setColonia(catalogoGenericoDB.getDescripcionCatalogo("KCOLONIA", derechohabiente));
		derechohabiente.setEstadoCivil(catalogoGenericoDB.getDescripcionCatalogo("KESTADOCIVIL", derechohabiente));
		derechohabiente.setClinicaServicio(catalogoGenericoDB.getDescripcionCatalogo("KCLINICASERVICIO", derechohabiente));
		derechohabiente.setParentesco(catalogoGenericoDB.getDescripcionCatalogo("WKPARENTESCO", derechohabiente));
		derechohabiente.setUsuario(usuarioDB.getUsuarioPreafiliacionByNoControlAndNoAfiliacion(derechohabiente.getNoControl(), derechohabiente.getNoPreAfiliacion()));
		derechohabiente.setEstatusDescripcion(catalogoGenericoDB.getDescripcionCatalogo("WKESTATUS", derechohabiente));
		
	}
	
	
	public ResponseEntity<?> getPersonaByNoControlNoPreafiliacion(InfoPersona infoPersona) {
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
			
		if (persona == null) 
			return new ResponseEntity<>(new Mensaje("No existe esa persona"), HttpStatus.CONFLICT);
		
		fillDerechohabiente(persona);
		return new ResponseEntity<>(persona, HttpStatus.OK);		
    }
	
	public ResponseEntity<?> getAfiliadoByNoControlNoPreafiliacion(long noControl, long noAfiliacion, long claveParentesco) {
		Derechohabiente afiliado =  personaDB.getAfiliadoByNoControlNoPreafiliacion(noControl, noAfiliacion, claveParentesco);
		
		if (afiliado == null) 
			if (claveParentesco == 0)
				return new ResponseEntity<>(new Mensaje("No existe el titular"), HttpStatus.CONFLICT);
			else 
				return new ResponseEntity<>(new Mensaje("No existe el beneficiario"), HttpStatus.CONFLICT);
		
		fillDerechohabiente(afiliado);
		return new ResponseEntity<>(afiliado, HttpStatus.OK);
    }
	
	
	public ResponseEntity<?> registraUsuario(boolean registroOnline, Derechohabiente persona, long claveParentesco){
		ResultadoValidacion resultadoValidacion = validaDatosUsuario(persona);
		
		if (!resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
	
		Usuario usuario = usuarioDB.getUsuarioByColumnaStringValor("LOGIN", persona.getEmail());
		
		if (usuario != null)
			return new ResponseEntity<>(new Mensaje("Ya existe un usuario con ese correo electrónico"), HttpStatus.CONFLICT);
		
		try{
			Derechohabiente oldPersona = null;
			
			// Revisamos si el registro trae curp sino haremos la consulta por noControl y NoAfiliacion
			// if(persona.getCurp() != null) {
				
			// Revisamos que la persona exista en nuestra base de datos
			 oldPersona =  personaDB.getPersonaByColumnaStringValor("CURP", persona.getCurp());
			
			// Sin encontramos un resultado en nuestra bd rechacamos la creacion del usuario
			if(oldPersona != null) {
				// Se valida si ya existe el registro del usuario
				Usuario usr  = usuarioDB.getUsuarioPreafiliacionByNoControlAndNoAfiliacion(oldPersona.getNoControl(),
																						   oldPersona.getNoPreAfiliacion());
				if (usr != null)
					return new ResponseEntity<>(new Mensaje("Ya existe una cuenta para el derechohabiente"), HttpStatus.CONFLICT);
				
				// Se crea la cuenta	
				ResultadoCreacionCuenta infoCuenta = creaCuenta(oldPersona, persona, INACTIVA);
				
				if (!infoCuenta.isEtatus())
					return new ResponseEntity<>( infoCuenta.getMensaje() , HttpStatus.INTERNAL_SERVER_ERROR);					
			}
			else {
				// Sino hacemos la consulta a la bd de issstep
				oldPersona = personaDB.getTrabajadorIssstepByColumnaStringValor("CURP", persona.getCurp());
				
				//Si encontramos un resultado en la base de datos ISSSTEP procedemos a la creacion del Derechohabiente y su usuario de la plataforma
				if(oldPersona != null) {
					/* int estatus = -1;
					
					Esto era para crear una cuenta por el administrador 
					if (persona.getEmail().equals("issstepregistro@gmail.com")) {
						oldPersona.setEmail("issstepregistro" + oldPersona.getNoControl() +  "@gmail.com");
						persona.setEmail("issstepregistro" + oldPersona.getNoControl() +  "@gmail.com");
						estatus = 1;
					} */
				
					if(personaDB.createDerechohabiente(oldPersona, false, 4) > 0) { 
						// Benefiario del trabajador
						
						ResultadoCreacionCuenta infoCuenta = creaCuenta(oldPersona, persona, INACTIVA);
						
						if (!infoCuenta.isEtatus())
							return new ResponseEntity<>( infoCuenta.getMensaje() , HttpStatus.INTERNAL_SERVER_ERROR);
						
						/* long idBeneficiario = beneficiarioDB.createBeneficiario(oldPersona.getNoControl(),  oldPersona, 0);
						if (idBeneficiario > 0) {
							if(creaUsuario(oldPersona, persona, estatus)>0) {
								for(Derechohabiente beneficiario : personaDB.getBeneficiariosByTrabajadorIssstep(oldPersona.getNoControl())) {
									personaDB.createDerechohabiente(beneficiario, false, 4);
									beneficiarioDB.createBeneficiario(oldPersona.getNoControl(), beneficiario, beneficiario.getClaveParentesco());
								}
							}
							else
								return new ResponseEntity<>(new Mensaje("Error al registrar al usuario"), HttpStatus.INTERNAL_SERVER_ERROR); 
						}
						else 
							return new ResponseEntity<>(new Mensaje("Error al registrar al beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR); */
					}
					else
						return new ResponseEntity<>(new Mensaje("Error al registrar al Derechohabiente"), HttpStatus.INTERNAL_SERVER_ERROR);
				}						
				// Sino rechacamos la creacion del usuario ya que este no esta registrado
				else 
					return new ResponseEntity<>(new Mensaje("No existe persona a registrar"), HttpStatus.CONFLICT);
			}
			
			//}
			
			return  new ResponseEntity<>(oldPersona, HttpStatus.CREATED);
		}
		catch(DataIntegrityViolationException e){
			System.err.println("Exception DataIntegrityViolationException PersonaService.guardaPersona");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e){
			System.err.println("Exception PersonaService.guardaPersona");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	ResultadoValidacion validaDatosUsuario(Derechohabiente persona) {
	
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEsValido(true);				
		
		if (persona.getCurp() == null ) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional la curp");
			return resultadoValidacion;
		}
		else {
			if (!Utils.esCURP(persona.getCurp())) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Formato de CURP invalido");
				return resultadoValidacion;
			}
		}
		
		if (persona.getEmail() == null ) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el correo electrónico");
			return resultadoValidacion;
		}
		else {
			if (!Utils.esEmail(persona.getEmail())) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Correo electrónico invalido");
				return resultadoValidacion;
			}			
		}
		
		if (persona.getUsuario() == null ) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el login y el password");
			return resultadoValidacion;
		}
		else {
			if (persona.getUsuario().getLogin() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el login");
				return resultadoValidacion;
			}
			else {
				if (!Utils.esEmail(persona.getUsuario().getLogin())) {
					resultadoValidacion.setEsValido(false);
					resultadoValidacion.setMensaje("Login invalido");
					return resultadoValidacion;
				}
			}
			
			if (persona.getUsuario().getPasswd() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el password");
				return resultadoValidacion;
			}
		}

		return resultadoValidacion;
	}
	
	ResultadoCreacionCuenta creaCuenta(Derechohabiente oldPersona, Derechohabiente persona, int estatus) {
		ResultadoCreacionCuenta resultadoCreacionCuenta = new ResultadoCreacionCuenta();
		Beneficiario  beneficiarioNuevo = null;
		
		// Se valida si ya existe; ya no tiene cuenta pero ya fue migrado con anticipacion
		beneficiarioNuevo = beneficiarioDB.getBeneficiario(oldPersona.getNoControl(), 
														   oldPersona.getNoControl(), 
														   oldPersona.getNoPreAfiliacion(),
														   0);
		long idBeneficiario;
		if (beneficiarioNuevo == null)
			idBeneficiario = beneficiarioDB.createBeneficiario(oldPersona.getNoControl(),  oldPersona, 0);
		else 
			idBeneficiario = beneficiarioNuevo.getNoBeneficiario();
			
		if (idBeneficiario > 0) {
			if(creaUsuario(oldPersona, persona, estatus)>0) {
				InfoPersona infoPersona = null;
				Derechohabiente derechohabienteNuevo = null;
				for(Derechohabiente beneficiario : personaDB.getBeneficiariosByTrabajadorIssstep(oldPersona.getNoControl())) {
					infoPersona = creaInforPersona(oldPersona.getNoControl(), 
												   beneficiario.getNoControl(), 
												   beneficiario.getNoPreAfiliacion(), 
												   0);
					// Se verifica si ya existe en la BD de derechohabientes
					derechohabienteNuevo = personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
					if (derechohabienteNuevo == null) {
						personaDB.createDerechohabiente(beneficiario, false, 4);
						
						// Se verifica si ya existe en la BD de beneficiarios
						beneficiarioNuevo = beneficiarioDB.getBeneficiario(oldPersona.getNoControl(), 
																		   beneficiario.getNoControl(), 
																		   beneficiario.getNoPreAfiliacion(),
																		   beneficiario.getClaveParentesco());
						if (beneficiarioNuevo == null)
							beneficiarioDB.createBeneficiario(oldPersona.getNoControl(), beneficiario, beneficiario.getClaveParentesco());
					}
				}
				resultadoCreacionCuenta.setEtatus(true);
				resultadoCreacionCuenta.setDerechohabiente(oldPersona);
				
			}
			else {
				resultadoCreacionCuenta.setEtatus(false);
				resultadoCreacionCuenta.setMensaje(new Mensaje("Error al registrar al usuario"));
				// return new ResponseEntity<>(, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else {
			resultadoCreacionCuenta.setEtatus(false);
			resultadoCreacionCuenta.setMensaje(new Mensaje("Error al registrar al beneficiario"));
			// return new ResponseEntity<>(new Mensaje("Error al registrar al beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return resultadoCreacionCuenta;	
	}
	
	
	public long creaUsuario(Derechohabiente oldPersona , Derechohabiente newPersona, int estatus) {
		String token = Utils.sha256(newPersona.getEmail());
		
		Usuario usuario = new Usuario();
		usuario.setClaveRol(2);
		usuario.setNoControl(oldPersona.getNoControl());
		usuario.setLogin(newPersona.getEmail());
		usuario.setPasswd(Hashing.sha256().hashString(newPersona.getUsuario().getPasswd(), Charsets.UTF_8).toString());
		usuario.setToken(token);
		usuario.setFechaRegistro(new Timestamp(new Date().getTime()));
		usuario.setEstatus(estatus);
		usuario.setNoAfiliacion(oldPersona.getNoPreAfiliacion());
		
		long claveUsuario = usuarioDB.createUsuario( 0, usuario );
				
		if(claveUsuario > 0) {
			//if (Utils.loadPropertie("ambiente").equals(PRODUCCION) || Utils.loadPropertie("ambiente").equals(PRUEBAS)){
		     //   mailService.prepareAndSendBienvenida(persona.getEmail(),persona.getNombreCompleto() ,persona.getEmail(),persona.gettUsuario().getToken(),persona.gettUsuario().getId());
		    //}else{
		        //Manda al correo de issstepregistro@gmail.com
			
			    oldPersona.setEmail(newPersona.getEmail());
				
				
			    oldPersona.setClaveUsuarioRegistro(claveUsuario);
				personaDB.actualiza(oldPersona);
				
				// if (!oldPersona.getEmail().equals("issstepregistro@gmail.com" + oldPersona.getNoControl() + "@gmail.com"))
				String ambiente = Utils.loadPropertie("ambiente");
				if (ambiente.equals("3"))
				 	mailService.prepareAndSendBienvenida(oldPersona.getEmail(), oldPersona.getNombreCompleto() ,
		        		 oldPersona.getEmail(), usuario.getToken(), oldPersona.getNoControl());
				else 
					mailService.prepareAndSendBienvenida("issstepregistro@gmail.com", oldPersona.getNombreCompleto() ,
			        		 oldPersona.getEmail(), usuario.getToken(), oldPersona.getNoControl());
		     //}	

		}
		return claveUsuario;
	}
	
	
	public ResponseEntity<?> activarRegistro(String token){
		try{	
			Usuario usuario = usuarioDB.getUsuarioByToken(token);
			if(usuario != null){
				if(usuario.getEstatus() == INACTIVA){
					
					usuario.setEstatus(1);
					usuario.setFechaUltimoAcceso(new Timestamp(new Date().getTime()));
					usuario.setToken(null);
					usuarioDB.actualiza(usuario);
					
					return new ResponseEntity<>(new Mensaje("Usuario activado correctamente"), HttpStatus.OK);
					
				}
				else {
					usuario.setFechaUltimoAcceso(new Timestamp(new Date().getTime()));
					usuario.setToken(null);
					usuarioDB.actualiza(usuario);
					return new ResponseEntity<>(new Mensaje("Usuario previamente activo"), HttpStatus.CONFLICT);
				}	
			}
			else {
				return new ResponseEntity<>(new Mensaje("Token invalido"), HttpStatus.CONFLICT);
			}
		}catch (Exception ex){
			ex.printStackTrace();
			System.err.println("Exception activarRegistro");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> recuperarPassword(ResetPassword resetPassword){
		try{	
			Usuario usuario = usuarioDB.getUsuarioByToken(resetPassword.getToken());
			if(usuario != null){
					usuario.setFechaUltimoAcceso(new Timestamp(new Date().getTime()));
					usuario.setToken(null);
					usuario.setPasswd(Hashing.sha256().hashString(resetPassword.getPassword(), Charsets.UTF_8).toString());
					usuarioDB.actualiza(usuario);
					
					return new ResponseEntity<>(new Mensaje("Password actualizado correctamente"), HttpStatus.OK);
					
			}
			else {
				return new ResponseEntity<>(new Mensaje("Token invalido"), HttpStatus.CONFLICT);
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			System.err.println("Exception recuperarPassword");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> registraDerechohabiente( Derechohabiente registroDerechohabiente ) {
		ResultadoValidacion resultadoValidacion =  validaDatosRegistro(registroDerechohabiente);
		
		if (!resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
		
		if  (getPersonaByCurp(registroDerechohabiente.getCurp()) != null) 
			return new ResponseEntity<>(new Mensaje("Ya exite un derechohabiente con esa Curp"), HttpStatus.CONFLICT);
		
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
		Derechohabiente derechohabienteTitular = null;
		
		if (usuario == null)
			return new ResponseEntity<>(new Mensaje("Usuario no logeado"), HttpStatus.BAD_REQUEST);
		
		
		boolean esAdmin = usuario.getClaveRol() == 1;
		
		// if (!esAdmin) 
		if (registroDerechohabiente.getClaveParentesco() == 0)
			return new ResponseEntity<>(new Mensaje("El alta de titulares no es permitido"), HttpStatus.BAD_REQUEST);
				
		try{	
			
			/* if (registroDerechohabiente.getClaveParentesco() == 0) {
				derechohabienteTitular = new Derechohabiente();
				derechohabienteTitular.setNoControl(0);
			}
			else
			{ */
				
			InfoPersona infoPersona = creaInforPersona(usuario.getNoControl(), 
													   usuario.getNoControl(), 
													   usuario.getNoControl(), 
													   0);
			// Se reucpera la informacion del titular
			derechohabienteTitular = personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
			
			if (derechohabienteTitular == null)
				return new ResponseEntity<>(new Mensaje("No existe el regitro del titular"), HttpStatus.BAD_REQUEST);	
			
			// registroDerechohabiente.setNoControl(usuario.getNoControl()); 
			inicializaConValoresDelTitular( registroDerechohabiente, derechohabienteTitular, usuario);
			
			// }ur
			
			//String[] fechaNac = registroDerechohabiente.getFechaNacimiento().split("/");
			//registroDerechohabiente.setFechaNacimiento(fechaNac[2] + "/" + fechaNac[1] + "/"+ fechaNac[0]);
					
			long estatusRegistro = personaDB.createDerechohabiente( registroDerechohabiente, esAdmin, 1);
			
			if (estatusRegistro == 0) 
				return new ResponseEntity<>(new Mensaje("No fue posible registrar al derechohabiente"), HttpStatus.INTERNAL_SERVER_ERROR);
				
			beneficiarioDB.createBeneficiario(registroDerechohabiente.getNoControl(), 
											  registroDerechohabiente, 
											  registroDerechohabiente.getClaveParentesco());
			
			return new ResponseEntity<>(registroDerechohabiente, HttpStatus.CREATED);					
			
		}
		catch(DataIntegrityViolationException e){
			System.err.println("Exception PersonaService.registraDerechohabiente");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e){
			System.err.println("Exception PersonaService.registraDerechohabiente");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	
	}
	
	InfoPersona creaInforPersona(long noControlTitular, long noControl, long noPreAfiliacion, long claveParentesco) {
		InfoPersona infoPersona = new InfoPersona();
		infoPersona.setNoControlTitular(noControlTitular);
		infoPersona.setNoControl(noControl);
		infoPersona.setNoPreAfiliacion(noPreAfiliacion); 
		infoPersona.setClaveParentesco(claveParentesco);
		
		return infoPersona;
	}
	
	void inicializaConValoresDelTitular(Derechohabiente registroDerechohabiente, 
										Derechohabiente derechohabienteTitular,
										Usuario usuario) {
		
		registroDerechohabiente.setDireccion(derechohabienteTitular.getDireccion()); 
		registroDerechohabiente.setTelefonoCasa(derechohabienteTitular.getTelefonoCasa());
		registroDerechohabiente.setTelefonoCelular(derechohabienteTitular.getTelefonoCelular());
		registroDerechohabiente.setCodigoPostal(derechohabienteTitular.getCodigoPostal());
		registroDerechohabiente.setClaveColonia(derechohabienteTitular.getClaveColonia());
		registroDerechohabiente.setFechaPreAfiliacion(derechohabienteTitular.getFechaPreAfiliacion());
		registroDerechohabiente.setClaveClinicaServicio(derechohabienteTitular.getClaveClinicaServicio());
		registroDerechohabiente.setClaveLocalidad(derechohabienteTitular.getClaveLocalidad());
		registroDerechohabiente.setClaveMunicipio(derechohabienteTitular.getClaveMunicipio());
		registroDerechohabiente.setClaveEstado(derechohabienteTitular.getClaveEstado());
		
		NumerosParaRegistro numerosParaRegistro = personaDB.getNextNumerosRegistro(registroDerechohabiente.getClaveParentesco(), registroDerechohabiente.getNoControl());
		
		// registroDerechohabiente.setNoControl(numerosParaRegistro.getNoControl());
		registroDerechohabiente.setNoPreAfiliacion(numerosParaRegistro.getNoPreAfiliacion());
		registroDerechohabiente.setFechaRegistro(new Timestamp(new Date().getTime()));
		registroDerechohabiente.setSituacion(1);
		registroDerechohabiente.setEstatus(1);
		registroDerechohabiente.setClaveUsuarioRegistro(usuario.getClaveUsuario());
		registroDerechohabiente.setClaveUsuarioModificacion(usuario.getClaveUsuario());
	}
	
	ResultadoValidacion validaDatosRegistro(Derechohabiente datosRegistro) {
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEsValido(true);
				
		if (datosRegistro.getClaveParentesco() != 0 && datosRegistro.getNoControl() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el numero de control");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getCurp() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo curp");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getRfc() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo rfc");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getClaveParentesco() == 0) {
			if (datosRegistro.getDireccion() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo direccion");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getTelefonoCasa() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCasa");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getTelefonoCelular() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCelular");
				return resultadoValidacion;
			}
						
			if (datosRegistro.getClaveClinicaServicio() == 0) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveClinicaServicio");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveColonia() == 0) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveColonia");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveEstado() == 0) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveEstado");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveLocalidad() == 0) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveLocalidad");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveMunicipio() == 0) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveMunicipio");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getCodigoPostal() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveCodigoPostal");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getFechaPreAfiliacion() == null) {
				resultadoValidacion.setEsValido(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo fechaPreAfiliacion");
				return resultadoValidacion;
			}
		}
		
		if (datosRegistro.getClaveEstadoCivil() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo claveEstadoCivil");
			return resultadoValidacion;
		}
		
		return resultadoValidacion;
		
	}
	
	public ResponseEntity<?> asignarBeneficiario(Beneficiario beneficiario) {
		ResultadoValidacion resultadoValidacion =  validaDatosBeneficiario(beneficiario);
		
		if (!resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
		
		
		CatalogoGenerico parentesco = catalogoGenericoDB.getRegistro("WKPARENTESCO", beneficiario.getClaveParentesco());
		
		if (parentesco == null)
			return new ResponseEntity<>(new Mensaje("Clave de parentesco invalida"), HttpStatus.BAD_REQUEST);
		
		if (beneficiario.getClaveParentesco() == 0)
			return new ResponseEntity<>(new Mensaje("No puede registrar un beneficiario con clave de titular"), HttpStatus.BAD_REQUEST);

		if (personaDB.existeBeneficiarioRegistradoById(beneficiario))
			return new ResponseEntity<>(new Mensaje("Ya existe un beneficiario registrado"), HttpStatus.BAD_REQUEST);
		
		if (beneficiario.getClaveParentesco() != 6 && beneficiario.getClaveParentesco() != 7) {
			if (personaDB.existeBeneficiarioRegistrado(beneficiario) == 1)
				return new ResponseEntity<>(new Mensaje("Ya existe un beneficiario registrado con ese parenteso"), HttpStatus.BAD_REQUEST);
			else 
				if (personaDB.existeBeneficiarioRegistrado(beneficiario) == 2)
					return new ResponseEntity<>(new Mensaje("No se pudo verificar si el beneficiario ya esta registrado"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		InfoPersona infoPersona = creaInforPersona(beneficiario.getNoControl(), beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), beneficiario.getClaveParentesco());
		
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
		
		// Sino tenemos resultado de nuestra base de datos vamos por los datos a la bd de issstep
		if(persona == null) {
			long tipoBeneficiario = (beneficiario.getNoControl() == beneficiario.getNoPreAfiliacion()) ? 0 : 1;
			persona = personaDB.getAfiliadoByNoControlNoPreafiliacion(beneficiario.getNoControl(), 
																	  beneficiario.getNoPreAfiliacion(),
																	  tipoBeneficiario);
			// persona.setNoControl(beneficiario.getNoControlTitular());
			personaDB.createDerechohabiente(persona, false, 4);
		}
		
		/* Beneficiario oldBeneficiario = beneficiarioDB.getBeneficiario(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), beneficiario.getClaveParentesco());
		
		if (oldBeneficiario != null)
			return new ResponseEntity<>(new Mensaje("Ya existe el beneficiario"), HttpStatus.CONFLICT); */
		
		try{
			
			persona.setSituacion(1);
			persona.setFechaRegistro(new Timestamp(new Date().getTime()));
				
			long noBeneficiario = beneficiarioDB.createBeneficiario(beneficiario.getNoControlTitular(), persona, beneficiario.getClaveParentesco());
			
			if (noBeneficiario == 0)
				return new ResponseEntity<>(new Mensaje("No fue posible asignar el beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
			
			if (noBeneficiario == -1)
				return new ResponseEntity<>(new Mensaje("Existe un problema de integridad en beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
			
			persona.setNoBeneficiario(noBeneficiario);
			
			// System.out.println(persona.getNombreCompleto());
			  
			return new ResponseEntity<>(noBeneficiario, HttpStatus.CREATED);
			 	 
		}
		catch(DataIntegrityViolationException e){
			System.err.println("Exception PersonaService.asignarBeneficiario");
			e.printStackTrace();
			return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e){
			System.err.println("Exception PersonaService.asignarBeneficiario");
			e.printStackTrace();
			return  new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private ResultadoValidacion validaDatosBeneficiario(Beneficiario beneficiario) {
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEsValido(true);
		
		if (beneficiario.getNoControlTitular() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el no. de control del titular");
			return resultadoValidacion;
		}
		
		if (beneficiario.getNoControl() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el no. de control del beneficiario");
			return resultadoValidacion;
		}
		
		if (beneficiario.getNoPreAfiliacion() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el no. de pre-afiliación del beneficiario");
			return resultadoValidacion;
		}
		
		if (beneficiario.getClaveParentesco() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional la clave del parentesco del beneficiario");
			return resultadoValidacion;
		}
		return resultadoValidacion;
	}
	
	public ResponseEntity<?> eliminarBeneficiario(long  idBeneficiario) {
		
		Beneficiario beneficiario = beneficiarioDB.getBeneficiarioById(idBeneficiario);
		
		if (beneficiario == null)
			return new ResponseEntity<>(new Mensaje("No existe el beneficiario"), HttpStatus.NOT_FOUND);
		
		if (beneficiarioDB.deleteBeneficiario( idBeneficiario) != 1)
			return new ResponseEntity<>(new Mensaje("No se pudo eliminar el beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
		
		return new ResponseEntity<>(new Mensaje("Beneficiario eliminado"), HttpStatus.OK);
	}
	
	public ResponseEntity<?> solicitudRecuperarPassword(String email) {
		Derechohabiente derechohabiente = personaDB.getPersonaByColumnaStringValor("EMAIL", email);
		
		if (derechohabiente != null) {
			String token = Utils.sha256(email);
			
			usuarioDB.actualizaToken( token, derechohabiente.getNoControl(), derechohabiente.getNoPreAfiliacion());
			
			mailService.prepareAndSendResetPass("issstepregistro@gmail.com", derechohabiente.getNombreCompleto(), token);
			
			return new ResponseEntity<>(new Mensaje("Solicitud de actualización enviada."), HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(new Mensaje("El correo electrónico no existe."), HttpStatus.CONFLICT);
		
	}
	
	public ResponseEntity<?> actualizarPassword( ActualizarPassword actualizarPassword) {	
		ResultadoValidacion resultadoValidacion =  validaDatosPassword(actualizarPassword);
		
		if (resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
		
		Usuario usuarioLogin = getInfoLogin();
		if (usuarioLogin == null)
			return new ResponseEntity<>(new Mensaje("Usuario no autentificado"), HttpStatus.BAD_REQUEST);
		
		String passwordActual = Hashing.sha256().hashString(actualizarPassword.getPasswordActual(), Charsets.UTF_8).toString();
		
		Usuario usuario = usuarioDB.getUsuarioPreafiliacionByNoControlAndNoAfiliacion(usuarioLogin.getNoControl(), usuarioLogin.getNoAfiliacion());
		/* System.out.println(passwordActual);
		System.out.println(usuario.getPasswd());*/
		
		if (!usuario.getPasswd().equals(passwordActual)) 
			return new ResponseEntity<>(new Mensaje("Password actual incorrecto "), HttpStatus.BAD_REQUEST);		
						
		usuario.setPasswd(Hashing.sha256().hashString(actualizarPassword.getPasswordNuevo(), Charsets.UTF_8).toString());
		usuarioDB.actualiza(usuario);
		
		return new ResponseEntity<>(usuario , HttpStatus.OK);	
				
    }
	
	ResultadoValidacion validaDatosPassword(ActualizarPassword actualizarPassword) {
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEsValido(true);
		
		if (actualizarPassword.getPasswordActual() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo passwordActual");
			return resultadoValidacion;
		}
		
		if (actualizarPassword.getPasswordNuevo() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo passwordNuevo");
			return resultadoValidacion;
		}
		
		return resultadoValidacion;
	}
	
	public ResponseEntity<?> actualizarDatos( Derechohabiente datosDerechohabiente) {	
		Usuario usuarioLogin = getInfoLogin();
		if (usuarioLogin == null)
			return new ResponseEntity<>(new Mensaje("Usuario no autentificado"), HttpStatus.BAD_REQUEST);
		
		boolean esAdmin = usuarioLogin.getRol().equals("ADMINISTRADOR");
		
		ResultadoValidacion resultadoValidacion =  validaDatosRegistro(datosDerechohabiente);
		
		if (!resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
		
		InfoPersona infoPersona = creaInforPersona(datosDerechohabiente.getNoControl(), 
												   datosDerechohabiente.getNoControl(),
												   datosDerechohabiente.getNoPreAfiliacion(),
												   datosDerechohabiente.getClaveParentesco());
		
		Derechohabiente derechohabiente = personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
		
		if (derechohabiente == null)
			return new ResponseEntity<>(new Mensaje("No existe el derechohabiente con número de Control: " + datosDerechohabiente.getNoControl() 
												  + " y numero de pre-afiliacion: " + datosDerechohabiente.getNoPreAfiliacion())
												  , HttpStatus.CONFLICT);		
			
		if (personaDB.actualizaDatos(esAdmin, datosDerechohabiente) == -1)
			return new ResponseEntity<>(new Mensaje("No se pudo actualizar el usuario con número de Control: " + datosDerechohabiente.getNoControl()), HttpStatus.INTERNAL_SERVER_ERROR);
		
		return new ResponseEntity<>(derechohabiente , HttpStatus.OK);				
    }
	
	ResultadoValidacion validaDatosAActualizar(ActualizarDatos actualizarDatos) {
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEsValido(true);
		
		if (actualizarDatos.getNoControl() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo noControl");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getNoPreAfiliacion() == 0) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo noPreAfiliacion");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getDireccion() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo direccion");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getTelefonoCasa() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCasa");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getTelefonoCelular() == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCelular");
			return resultadoValidacion;
		}
				
		return resultadoValidacion;					
	}
	
	public ResponseEntity<?> getDerechohabientesPorEstatusDeValidacion( int estatusValidacion) {	
		if (estatusValidacion < 0 || estatusValidacion > 2)
			return new ResponseEntity<>(new Mensaje("Estatus de validacion incorrecto"), HttpStatus.BAD_REQUEST);
		
		List<InfoDerechohabiente> derechohabientes = personaDB.getDerechohabientesPorEstatusDeValidacion( estatusValidacion );
			
		return new ResponseEntity<>(derechohabientes, HttpStatus.OK);	
    }
	
	// funcion que regrerara los beneficiarios de algun trabador
	public ResponseEntity<?> getBeneficiarios(boolean incluirTitular, long noControl) {	
		Usuario usuarioLogin = getInfoLogin();
		if (usuarioLogin == null)
			return new ResponseEntity<>(new Mensaje("Usuario no autentificado"), HttpStatus.BAD_REQUEST);
		
		List<Derechohabiente> listaBeneficiarios = personaDB.getBeneficiariosByDerechohabiente(incluirTitular, noControl);
		
		if (listaBeneficiarios != null) {
			for(Derechohabiente dere: listaBeneficiarios){
				fillDerechohabiente(dere);
			}
			return new ResponseEntity<>(listaBeneficiarios, HttpStatus.OK);
		}
		else {		
			if (!usuarioLogin.getRol().equals("ADMINISTRADOR"))
				return new ResponseEntity<>( new ArrayList[0], HttpStatus.NOT_FOUND) ;
			
			Derechohabiente derechohabiente = null;
			
			InfoPersona infoPersona = creaInforPersona(noControl, noControl, noControl, 0);
			derechohabiente = personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
			
			// Verificamos si se encontrol en la bd de derecohabientes
			if (derechohabiente == null) {
				Derechohabiente trabajador = null;
				trabajador = personaDB.getPersonaByNoControlNoAfiliacionIssstep(noControl, noControl);
				
				// Verificamos si se encontrol en la bd de afiliados
				if(trabajador != null) {
					// Si no se encontro se crear el registro del titular en la bd de derechohabientes
					if(personaDB.createDerechohabiente(trabajador, false, 4) > 0) { 
						// Se recuperan los benefiarios y se registran en la bd de derechochoabientes
						beneficiarioDB.createBeneficiario(trabajador.getNoControl(),  trabajador, 0);
						for(Derechohabiente beneficiario : personaDB.getBeneficiariosByTrabajadorIssstep(trabajador.getNoControl())) {
							personaDB.createDerechohabiente(beneficiario, false, 4);
							beneficiarioDB.createBeneficiario(trabajador.getNoControl(), beneficiario, beneficiario.getClaveParentesco());
						}
						return getBeneficiarios(true, noControl);
					}
					else
						return new ResponseEntity<>(new Mensaje("No fue posible registrar la información del trabajador para realizar el proceso de pre-afiliación: " + noControl), HttpStatus.INTERNAL_SERVER_ERROR);				
				}
				else 
					return new ResponseEntity<>(new Mensaje("No existe información para el número de control: " + noControl), HttpStatus.BAD_REQUEST);
			}
			else {
				beneficiarioDB.createBeneficiario(derechohabiente.getNoControl(),  derechohabiente, 0);
				Derechohabiente benef = null;
				
				List<Derechohabiente> beneficiarios = personaDB.getBeneficiariosByTrabajadorIssstep(derechohabiente.getNoControl());
				
				for(Derechohabiente beneficiario: beneficiarios) {
					infoPersona = creaInforPersona(beneficiario.getNoControl(), beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), 0);
					benef = personaDB.getPersonaByNoControlNoPreafiliacion(infoPersona);
					if (benef == null) {
						personaDB.createDerechohabiente(beneficiario, false, 4);
						beneficiarioDB.createBeneficiario(derechohabiente.getNoControl(), beneficiario, beneficiario.getClaveParentesco());	
					}
				}
				return getBeneficiarios(true, noControl);			
			}			
		}		
    }
	
	public ResponseEntity<?> getDocumentacionBeneficiarios(boolean incluirTitular, long noControl) {		
		List<DocumentosFaltantes> listaDocumentosFaltantes = personaDB.getDocumentacionByDerechohabiente(incluirTitular, noControl);
		
		actualizaDocumentosOpcioneles(listaDocumentosFaltantes);
		
		if (listaDocumentosFaltantes != null)
			return new ResponseEntity<>(listaDocumentosFaltantes, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new ArrayList[0] , HttpStatus.OK);
		
    }
	
	Usuario getInfoLogin() {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		if (user == "anonymousUser") 
			return null;
			
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
		
		
		return usuario;
	}
	
	boolean esPawwordActual( Usuario usuario, String passwordActual) {
		
		return true;
	}
	
	public ResponseEntity<?> buscarInformacionEnPreafiliacion(boolean enPreafiliacion, DatoABuscar datoABuscar, boolean incluirBeneficario) {
		if (datoABuscar.getDato() == null)
			return new ResponseEntity<>(new Mensaje("Debe proporcionar el campo dato"), HttpStatus.BAD_REQUEST);
		
		String campo = "NOMBRE";
		boolean esValorNumerico = false;
		
		esValorNumerico = Utils.esNumero(datoABuscar.getDato()) ;
		
		if (esValorNumerico) 
			campo = "";
		else 
			if (Utils.esPatronCURP(datoABuscar.getDato()))
				campo = "CURP";
		
		// System.out.println("Campo ==> " + campo);
		List<ResultadoBusqueda> resultadoBusqueda;
		
		if (enPreafiliacion)
			resultadoBusqueda = personaDB.getInformacionPreAfiliaconByCampo(campo, datoABuscar.getDato(), esValorNumerico);
		else 
			resultadoBusqueda = personaDB.getInformacionAfiliaconByCampo(campo, datoABuscar.getDato(), esValorNumerico, incluirBeneficario);
		
		
		for(ResultadoBusqueda result: resultadoBusqueda){
			result.setParentesco(catalogoGenericoDB.getDescripcionParentesco(result.getClaveParentesco()));
		}
		
		if (resultadoBusqueda != null)
			return new ResponseEntity<>(resultadoBusqueda, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new ArrayList[0] , HttpStatus.OK);
		
    }
	
	public ResponseEntity<?> updateEstatusByNoControlAndNoPreAfiliacion(long noControl, long noPreAfiliacion, int estatus) {
		if (estatus < 0 && estatus > catalogoGenericoDB.getUltimoEstatus())
			return new ResponseEntity<>(new Mensaje("Estatus no valido"), HttpStatus.BAD_REQUEST);
		
		InfoPersona infoPersona = creaInforPersona(noControl, noControl, noPreAfiliacion, 0);
		
		Derechohabiente afiliado =  personaDB.getPersonaByNoControlNoPreafiliacion( infoPersona );
		
		if (afiliado == null) 
			return new ResponseEntity<>(new Mensaje("No existe el derechohabiente"), HttpStatus.CONFLICT);
		
		if (estatus == 9) {
			List<DocumentosFaltantes> listaDocumentosFaltantes = personaDB.getDocumentacionByDerechohabiente(true, noControl);
			
			int numDocsTitular = getNoDocumentosFaltantes(listaDocumentosFaltantes, noControl, noControl);
			
			if (numDocsTitular != 0)
				/* if (afiliado.getClaveParentesco() == 0 )
					return new ResponseEntity<>(new Mensaje("Aún tiene documentación pendiente"), HttpStatus.CONFLICT);
				else	*/
					return new ResponseEntity<>(new Mensaje("El titular aún tiene documentación pendiente"), HttpStatus.CONFLICT);
			
			// Se verifican el numero de documentos si no es titular
			// if (afiliado.getClaveParentesco() != 0) {
				int numDocsBeneficiario = getNoDocumentosFaltantes( listaDocumentosFaltantes, 
																	noControl, 
																	noPreAfiliacion);
				if ( numDocsBeneficiario > 0) 
					return new ResponseEntity<>(new Mensaje("Aún tiene documentación pendiente"), HttpStatus.CONFLICT);
			// }
		}
		
		if(!usuarioDB.actualizaEstatusDerechohabiente(noControl, noPreAfiliacion, estatus))
			return new ResponseEntity<>(new Mensaje("No fue posible actualizar el estatus"), HttpStatus.INTERNAL_SERVER_ERROR);
		
		return new ResponseEntity<>(new Mensaje("Actualizacion exitosa"), HttpStatus.OK);
    }
	
	int getNoDocumentosFaltantes(List<DocumentosFaltantes> listaDocumentosFaltantes, 
								 long noControl, 
								 long noPreAfiliacion) {		
		int docNoValidos = 0;
		actualizaDocumentosOpcioneles(listaDocumentosFaltantes);
		
		for(DocumentosFaltantes doc: listaDocumentosFaltantes) {			
			if (doc.getNoControl() == noControl &&  doc.getNoPreAfiliacion() == noPreAfiliacion && 
				doc.getEstatus() != 1 && doc.getEsObligatorio() == 1) 
				docNoValidos++;
		}
		
		return docNoValidos;
	}
	
	private void actualizaDocumentosOpcioneles(List<DocumentosFaltantes> listaDocumentosFaltantes) {
		for(DocumentosFaltantes doc: listaDocumentosFaltantes) {
			if (doc.getClaveTipoArchivo() == CONST_ESTUDIOS && (doc.getClaveParentesco() == HIJO || doc.getClaveParentesco() == HIJA ))
				doc.setEsObligatorio(validaHijos(doc));
		}
	}
	
	private int validaHijos(DocumentosFaltantes doc) {
		int edad = calculaEdad(doc.getFechaNacimiento());
		System.out.println(edad);
		if (edad > LIMITE_EDAD) 
			return 1;	
		return 0;
	}
	
	private int calculaEdad(Date fechaNacimiento) {
		Calendar hoy = Calendar.getInstance();		
		Calendar fechaNac = dateToCalendar(fechaNacimiento);
		
		int numD = hoy.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);
		int numM = hoy.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
		int numA = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
		
		if (numM < 0 || (numM == 0  && numD < 0))
			numA--;
		
		return numA;
	}
	
	private Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}
	
	
}
