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
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.InfoDerechohabiente;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.utils.Utils;



@Service
public class DerechohabienteService {
	private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
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
		
		fillDerechohabiente(persona);
		
		System.out.println("Terminacion");
		if (persona != null)
			return new ResponseEntity<>(persona, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new Mensaje("No existe persona con esa curp"), HttpStatus.NO_CONTENT);
		
    }
	
	public ResponseEntity<?> getPersonaByNombre(Derechohabiente persona, HttpServletResponse response) {
		Derechohabiente personaOld =  personaDB.getPersonaByNombre(persona, response);
		
		if (personaOld != null)
			return new ResponseEntity<>(personaOld, HttpStatus.OK);
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
	
	public ResponseEntity<?> getPersonaById(long id) {
		Derechohabiente persona =  personaDB.getPersonaById(id);
	
		if (persona != null)
			return new ResponseEntity<>(persona, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new Mensaje("No existe esa persona"), HttpStatus.CONFLICT);
		
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
						if(creaUsuario(oldPersona, persona)>0) {
							for(Derechohabiente beneficiario : personaDB.getBeneficiariosByTrabajadorIssstep(oldPersona.getNoControl())) {
								personaDB.createDerechohabiente(beneficiario);
								beneficiarioDB.createBeneficiario(beneficiario, beneficiario.getClaveParentesco());
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
	
	public ResponseEntity<?> asignarBeneficiario( Beneficiario beneficiario) {
		Beneficiario oldBeneficiario = beneficiarioDB.getBeneficiario(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), beneficiario.getClaveParentesco());
		
		if (oldBeneficiario != null)
			return new ResponseEntity<>(new Mensaje("Ya existe el beneficiario"), HttpStatus.CONFLICT);
		
		try{
			Derechohabiente oldPersona = personaDB.getPersonaById(beneficiario.getNoControl());
			
			if (oldPersona == null) 
				return new ResponseEntity<>(new Mensaje("No existe el derechohabiente"), HttpStatus.CONFLICT);
			
			String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
			Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
			
			oldPersona.setSituacion(1);
			oldPersona.setFechaRegistro(new Timestamp(new Date().getTime()));
			oldPersona.setClaveUsuarioRegistro(usuario.getClaveUsuario());  // Cambiarlo por la de la informacion del logeo
				
			long noBeneficiario = beneficiarioDB.createBeneficiario(oldPersona, beneficiario.getClaveParentesco());
			
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
		
		Usuario usuario = usuarioDB.getUsuarioByColumnaStringValor("LOGIN", actualizarPassword.getEmail());
		
		if (usuario == null)
			return new ResponseEntity<>(new Mensaje("No existe el usuario con email: " + actualizarPassword.getEmail()), HttpStatus.CONFLICT);		
				
		usuario.setPasswd(Hashing.sha256().hashString(actualizarPassword.getPassword(), Charsets.UTF_8).toString());
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
		public ResponseEntity<?> getBeneficiarios(boolean incluirTitular, long claveUsuarioRegistro) {
			String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
			Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
		
			List<Derechohabiente> listaBeneficiarios = personaDB.getBeneficiariosByDerechohabiente(usuario.getNoControl());
			
			for(Derechohabiente dere: listaBeneficiarios){
				fillDerechohabiente(dere);
			}
			
			if (listaBeneficiarios != null)
				return new ResponseEntity<>(listaBeneficiarios, HttpStatus.OK);
			
			else
				return new ResponseEntity<>(new ArrayList[0] , HttpStatus.OK);
			
	    }
	
}
