package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.*;


import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import issstep.afiliacion.db.DerechohabienteDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.db.BeneficiarioDB;
import issstep.afiliacion.db.CatalogoGenericoDB;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.ResetPassword;
import issstep.afiliacion.model.ResultadoBusqueda;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.InfoDerechohabiente;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.DatoABuscar;
import issstep.afiliacion.utils.Utils;



@Service
public class DerechohabienteService {
	private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
	private static final String expRegParaNumero = "^[0-9]+";
	//private static final String expRegParaCURP = "^([A-Za-z]{1}[AEIOUaeiou]{1}[A-Za-z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HMhm]{1}([A|a][S|s]|[B|b][C|c]|[B|b][S|s]|[C|c][C|c]|[C|c][S|s]|[C|c][H|h]|[C|c][L|l]|[C|c][M|m]|[D|d][F|f]|[D|d][G|g]|[G|g][T|t]|[G|g][R|r]|[H|h][G|g]|[J|j][C|c]|[M|m][C|c]|[M|m][N|n]|[M|m][S|s]|[N|n][T|t]|[N|n][L|l]|[O|o][C|c]|[P|p][L|l]|[Q|q][T|t]|[Q|q][R|r]|[S|s][P|p]|[S|s][L|l]|[S|s][R|r]|[T|t][C|c]|[T|t][S|s]|[T|t][L|l]|[V|v][Z|z]|[Y|y][N|n]|[Z|z][S|s]|[N|n][E|e])[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]{3}[0-9A-Za-z]{2})$";
	private static final String expRegParaCURP = "^([A-Z]{4}[0-9]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{6}[A-Z0-9]{1,2})$";
	
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
		
		// Revisamos que la persona exista en nuestra base de datos
		Derechohabiente persona =  personaDB.getPersonaByColumnaStringValor("CURP", curp);
			
		// Sino tenemos resultado de nuestra base de datos vamos por los datos a la bd de issstep
		if(persona == null) 
			persona = personaDB.getTrabajadorIssstepByColumnaStringValor("CURP", curp);
			
		System.out.println("Terminacion");
		if (persona != null) {
			fillDerechohabiente(persona);
			return new ResponseEntity<>(persona, HttpStatus.OK);
		}
		
		else
			return new ResponseEntity<>(new Mensaje("No existe persona con esa curp"), HttpStatus.NO_CONTENT);
		
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
				return new ResponseEntity<>(new Mensaje("No existe persona con esos datos"), HttpStatus.CONFLICT);	
		}
		
    }
	
	void fillDerechohabiente(Derechohabiente derechohabiente){
		
		derechohabiente.setEstado(catalogoGenericoDB.getDescripcionCatalogo("KESTADO", derechohabiente));
		derechohabiente.setMunicipio(catalogoGenericoDB.getDescripcionCatalogo("KMUNICIPIO", derechohabiente));
		derechohabiente.setLocalidad(catalogoGenericoDB.getDescripcionCatalogo("KLOCALIDAD", derechohabiente));
		derechohabiente.setColonia(catalogoGenericoDB.getDescripcionCatalogo("KCOLONIA", derechohabiente));
		derechohabiente.setEstadoCivil(catalogoGenericoDB.getDescripcionCatalogo("KESTADOCIVIL", derechohabiente));
		derechohabiente.setClinicaServicio(catalogoGenericoDB.getDescripcionCatalogo("KCLINICASERVICIO", derechohabiente));
		derechohabiente.setParentesco(catalogoGenericoDB.getDescripcionCatalogo("KPARENTESCO", derechohabiente));
		derechohabiente.setUsuario(usuarioDB.getUsuarioPreafiliacionByNoControlAndNoAfiliacion(derechohabiente.getNoControl(), derechohabiente.getNoPreAfiliacion()));
		
		
	}
	
	
	public ResponseEntity<?> getPersonaByNoControlNoPreafiliacion(long noControl, long noPreAfiliacion) {
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(noControl, noPreAfiliacion);
			
		if (persona != null) {
			fillDerechohabiente(persona);
			return new ResponseEntity<>(persona, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(new Mensaje("No existe esa persona"), HttpStatus.CONFLICT);
		
    }
	
	public ResponseEntity<?> getAfiliadoByNoControlNoPreafiliacion(long noControl, long noAfiliacion, long claveParentesco) {
		Derechohabiente afiliado =  personaDB.getAfiliadoByNoControlNoPreafiliacion(noControl, noAfiliacion, claveParentesco);
		
		if (afiliado != null) {
			fillDerechohabiente(afiliado);
			return new ResponseEntity<>(afiliado, HttpStatus.OK);
		}
		
		else
			if (claveParentesco == 0)
				return new ResponseEntity<>(new Mensaje("No existe el titular"), HttpStatus.CONFLICT);
			else 
				return new ResponseEntity<>(new Mensaje("No existe el beneficiario"), HttpStatus.CONFLICT);
		
    }
	
	
	public ResponseEntity<?> registraUsuario(boolean registroOnline, Derechohabiente persona, long claveParentesco){
		try{
			Derechohabiente oldPersona = null;
			//Revisamos si el registro trae curp sino haremos la consulta por noControl y NoAfiliacion
			if(persona.getCurp() != null) {
				// Revisamos que la persona exista en nuestra base de datos
				 oldPersona =  personaDB.getPersonaByColumnaStringValor("CURP", persona.getCurp());
				
				// Sin encontramos un resultado en nuestra bd rechacamos la creacion del usuario
				if(oldPersona != null)
					return new ResponseEntity<>(new Mensaje("Se encontro un registro en nuestra bd"), HttpStatus.CONFLICT);
				// Sino hacemos la consulta a la bd de issstep
				else 
					oldPersona = personaDB.getTrabajadorIssstepByColumnaStringValor("CURP", persona.getCurp());
					
				//Si encontramos un resultado en la base de datos ISSSTEP procedemos a la creacion del Derechohabiente y su usuario de la plataforma
				if(oldPersona != null) {
					if(personaDB.createDerechohabiente(oldPersona) > 0) { 
						// Benefiario del trabjador
						beneficiarioDB.createBeneficiario(oldPersona.getNoControl(),  oldPersona, 0);
						if(creaUsuario(oldPersona, persona)>0) {
							for(Derechohabiente beneficiario : personaDB.getBeneficiariosByTrabajadorIssstep(oldPersona.getNoControl())) {
								personaDB.createDerechohabiente(beneficiario);
								beneficiarioDB.createBeneficiario(oldPersona.getNoControl(), beneficiario, beneficiario.getClaveParentesco());
							}
						}
						else
							return new ResponseEntity<>(new Mensaje("Error al registrar al usuario"), HttpStatus.INTERNAL_SERVER_ERROR);
					}
					else
						return new ResponseEntity<>(new Mensaje("Error al registrar al Derechohabiente"), HttpStatus.INTERNAL_SERVER_ERROR);
				}
					
				// Sino rechacamos la creacion del usuario 
				else 
					return new ResponseEntity<>(new Mensaje("No existe persona a registrar"), HttpStatus.CONFLICT);
			
			}
			
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
	
	
	public long creaUsuario(Derechohabiente oldPersona , Derechohabiente newPersona) {
		String token = Utils.sha256(newPersona.getEmail());
		
		Usuario usuario = new Usuario();
		usuario.setClaveRol(2);
		usuario.setNoControl(oldPersona.getNoControl());
		usuario.setLogin(newPersona.getEmail());
		usuario.setPasswd(Hashing.sha256().hashString(newPersona.getUsuario().getPasswd(), Charsets.UTF_8).toString());
		usuario.setToken(token);
		usuario.setFechaRegistro(new Timestamp(new Date().getTime()));
		usuario.setEstatus(-1);
		usuario.setNoAfiliacion(oldPersona.getNoPreAfiliacion());
		
		long claveUsuario = usuarioDB.createUsuario( 0, usuario );
				
		if(claveUsuario > 0) {
			//if (Utils.loadPropertie("ambiente").equals(PRODUCCION) || Utils.loadPropertie("ambiente").equals(PRUEBAS)){
		     //   mailService.prepareAndSendBienvenida(persona.getEmail(),persona.getNombreCompleto() ,persona.getEmail(),persona.gettUsuario().getToken(),persona.gettUsuario().getId());
		    //}else{
		        //Manda al correo de fdsditco@gmail.com
			
			    oldPersona.setEmail(newPersona.getEmail());
				
				
			    oldPersona.setClaveUsuarioRegistro(claveUsuario);
				personaDB.actualiza(oldPersona);
			
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
				if(usuario.getEstatus() == -1){
					
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
	
	public ResponseEntity<?> registraDerechohabiente( Derechohabiente derechohabiente ) {
		try{
			
			Derechohabiente oldderechohabiente = personaDB.getPersonaByColumnaStringValor("CURP", derechohabiente.getCurp());
			
			if (oldderechohabiente != null) {
				System.out.println("CURP existente");
				return new ResponseEntity<>(new Mensaje("El derechohabiente ya esta registrado"), HttpStatus.CONFLICT);
			}
			/*Usuario usuario = new Usuario();
			
			usuario.setClaveRol(2);
			usuario.setLogin("");
			usuario.setPasswd("");
			usuario.setToken("");
			usuario.setFechaRegistro(new Timestamp(new Date().getTime()));
			usuario.setEstatus(1);
			usuario.setNoControl(derechohabiente.getNoControl());
			
			long claveUsuario = usuarioDB.createUsuario(claveParentesco, usuario);
			
			if (claveUsuario == 0) 
				return new ResponseEntity<>(new Mensaje("No fue posible crear su registro como usuario"), HttpStatus.CONFLICT);
			
			derechohabiente.setClaveUsuarioRegistro(claveUsuario); */
			derechohabiente.setFechaRegistro(new Timestamp(new Date().getTime()));
			derechohabiente.setFechaPreAfiliacion(new Timestamp(new Date().getTime()));
			long estatusRegistro = personaDB.createDerechohabiente( derechohabiente);
			
			if (estatusRegistro == 0) {
				System.out.println("No registrado");
				return new ResponseEntity<>(new Mensaje("No fue posible registrar al derechohabiente"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if (estatusRegistro == -1) {
				System.out.println("Integridad");
				return new ResponseEntity<>(new Mensaje("No de control y no de pre-aficiliacion duplicados"), HttpStatus.CONFLICT);
			}
			
			// return registraUsuario( false, derechohabiente, claveParentesco );
			
			return new ResponseEntity<>(derechohabiente, HttpStatus.CREATED);					
			
		}
		catch(DataIntegrityViolationException e){
			System.err.println("Exception PersonaService.guardaPersona");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e){
			System.err.println("Exception PersonaService.guardaPersona");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	
	}
	
	public ResponseEntity<?> asignarBeneficiario(Beneficiario beneficiario) {
		
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion());
		
		// Sino tenemos resultado de nuestra base de datos vamos por los datos a la bd de issstep
		if(persona == null) {
			
			persona = personaDB.getPersonaByNoControlNoAfiliacionIssstep(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion());
			persona.setNoControl(beneficiario.getNoControlTitular());
			personaDB.createDerechohabiente(persona);
		}
		
		Beneficiario oldBeneficiario = beneficiarioDB.getBeneficiario(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), beneficiario.getClaveParentesco());
		
		if (oldBeneficiario != null)
			return new ResponseEntity<>(new Mensaje("Ya existe el beneficiario"), HttpStatus.CONFLICT);
		
		try{
			
			// Derechohabiente oldPersona = personaDB.getPersonaById(beneficiario.getNoControl());
			
			/* if (oldPersona == null) 
				return new ResponseEntity<>(new Mensaje("No existe el derechohabiente"), HttpStatus.CONFLICT); */
			
			String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
			Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
			
			persona.setSituacion(1);
			persona.setFechaRegistro(new Timestamp(new Date().getTime()));
			// persona.setClaveUsuarioRegistro(usuario.getClaveUsuario());  // Cambiarlo por la de la informacion del logeo
				
			long noBeneficiario = beneficiarioDB.createBeneficiario(beneficiario.getNoControlTitular(), persona, beneficiario.getClaveParentesco());
			
			 if (noBeneficiario == 0)
				 return new ResponseEntity<>(new Mensaje("No fue posible asignar el beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
			  
			 return new ResponseEntity<>(noBeneficiario, HttpStatus.CREATED);
			 	 
		}
		catch(DataIntegrityViolationException e){
			System.err.println("Exception PersonaService.asignarBeneficiario");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e){
			System.err.println("Exception PersonaService.asignarBeneficiario");
			e.printStackTrace();
			return  new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> eliminarBeneficiario(long  idBeneficiario) {
		
		Beneficiario beneficiario = beneficiarioDB.getBeneficiarioById(idBeneficiario);
		
		if (beneficiario == null)
			return new ResponseEntity<>(new Mensaje("No existe el beneficiario"), HttpStatus.BAD_REQUEST);
		
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
		Usuario usuarioLogin = getInfoLogin();
		if (usuarioLogin == null)
			return new ResponseEntity<>(new Mensaje("Usuario no autentificado"), HttpStatus.BAD_REQUEST);
		
		String passwordActual = Hashing.sha256().hashString(actualizarPassword.getPasswordActual(), Charsets.UTF_8).toString();
		
		Usuario usuario = usuarioDB.getUsuarioPreafiliacionByNoControlAndNoAfiliacion(usuarioLogin.getNoControl(), usuarioLogin.getNoAfiliacion());
		System.out.println(passwordActual);
		System.out.println(usuario.getPasswd());
		
		if (!usuario.getPasswd().equals(passwordActual)) 
			return new ResponseEntity<>(new Mensaje("Password actual incorrecto "), HttpStatus.BAD_REQUEST);		
						
		usuario.setPasswd(Hashing.sha256().hashString(actualizarPassword.getPasswordNuevo(), Charsets.UTF_8).toString());
		usuarioDB.actualiza(usuario);
		
		return new ResponseEntity<>(usuario , HttpStatus.OK);	
				
    }
	
	public ResponseEntity<?> actualizarDatos( ActualizarDatos actualizarDatos) {	
		
		Derechohabiente derechohabiente = personaDB.getPersonaById(actualizarDatos.getNoControl());
		
		if (derechohabiente == null)
			return new ResponseEntity<>(new Mensaje("No existe el usuario con número de Control: " + actualizarDatos.getNoControl()), HttpStatus.CONFLICT);		
			
		if (personaDB.actualizaDatos(actualizarDatos) == -1)
			return new ResponseEntity<>(new Mensaje("No se pudo actualizar el usuario con número de Control: " + actualizarDatos.getNoControl()), HttpStatus.INTERNAL_SERVER_ERROR);
		
		return new ResponseEntity<>(derechohabiente , HttpStatus.OK);				
    }
	
	public ResponseEntity<?> getDerechohabientesPorEstatusDeValidacion( int estatusValidacion) {	
		if (estatusValidacion < 0 || estatusValidacion > 2)
			return new ResponseEntity<>(new Mensaje("Estatus de validacion incorrecto"), HttpStatus.BAD_REQUEST);
		
		List<InfoDerechohabiente> derechohabientes = personaDB.getDerechohabientesPorEstatusDeValidacion( estatusValidacion );
			
		return new ResponseEntity<>(derechohabientes, HttpStatus.OK);	
    }
	
	// funcion que regrerara los beneficiarios de algun trabador
	public ResponseEntity<?> getBeneficiarios(boolean incluirTitular, long noControl) {		
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
	
		List<Derechohabiente> listaBeneficiarios = personaDB.getBeneficiariosByDerechohabiente(incluirTitular, noControl);
		
		for(Derechohabiente dere: listaBeneficiarios){
			fillDerechohabiente(dere);
		}
		
		if (listaBeneficiarios != null)
			return new ResponseEntity<>(listaBeneficiarios, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new ArrayList[0] , HttpStatus.OK);
		
    }
	
	Usuario getInfoLogin() {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("N control ===> ");
		System.out.println(user);
		if (user == "anonymousUser") 
			return null;
			
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
		
		
		return usuario;
	}
	
	boolean esPawwordActual( Usuario usuario, String passwordActual) {
		
		return true;
	}
	
	public ResponseEntity<?> buscarInformacionEnPreafiliacion(boolean enPreafiliacion, DatoABuscar datoABuscar) {
		Pattern numero = Pattern.compile(expRegParaNumero);
		Matcher esNumero = numero.matcher(datoABuscar.getDato());
		String campo = "NOMBRE";
		boolean esValorNumerico = false;
		
		esValorNumerico = esNumero.find();
		
		if (esValorNumerico) 
			campo = "";
		else {
			Pattern curp = Pattern.compile(expRegParaCURP);
			Matcher esCURP = curp.matcher(datoABuscar.getDato());
			
			if (esCURP.find())
				campo = "CURP";
		}
		
		System.out.println("Campo ==> " + campo);
		List<ResultadoBusqueda> resultadoBusqueda;
		
		if (enPreafiliacion)
			resultadoBusqueda = personaDB.getInformacionPreAfiliaconByCampo(campo, datoABuscar.getDato(), esValorNumerico);
		else 
			resultadoBusqueda = personaDB.getInformacionAfiliaconByCampo(campo, datoABuscar.getDato(), esValorNumerico);
		
		
		for(ResultadoBusqueda result: resultadoBusqueda){
			result.setParentesco(catalogoGenericoDB.getDescripcionParentesco(result.getClaveParentesco()));
		}
		
		if (resultadoBusqueda != null)
			return new ResponseEntity<>(resultadoBusqueda, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new ArrayList[0] , HttpStatus.OK);
		
    }

}
