package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import issstep.afiliacion.db.DerechohabienteDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.utils.Utils;



@Service
public class DerechohabienteService {
	private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
	@Autowired
	DerechohabienteDB personaDB;
	
	@Autowired
	UsuarioDB usuarioDB;
	
	@Autowired
	MailService mailService;
		
	public ResponseEntity<?> getPersonaByCurp(String curp) {
		Derechohabiente persona =  personaDB.getPersonaByColumnaStringValor("CURP", curp);
		System.out.println("Terminacion");
		if (persona != null)
			return new ResponseEntity<>(persona, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new Mensaje("No existe persona con esa curp"), HttpStatus.CONFLICT);
		
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
			Derechohabiente oldPersona;
			
			oldPersona = personaDB.getPersonaByColumnaStringValor("CURP", persona.getCurp());
			if (!registroOnline)
			{
				Usuario usuario = new Usuario();
				usuario.setPasswd("");
				usuario.setLogin("");
				
				persona.setUsuario(usuario);		
			}
			
			/*if(persona.getCurp() != null && !(personaDB.getPersonaByCurp(persona.getCurp())!= null)){
				return new ResponseEntity<>(new Mensaje("La CURP ya está registrada."), HttpStatus.CONFLICT);
			}
			if(personaDB.getPersonaByEmail(persona.getEmail()) != null){
				return new ResponseEntity<>(new Mensaje("Email Duplicado"), HttpStatus.CONFLICT);
			}*/
			
			if(oldPersona == null) 
				return new ResponseEntity<>(new Mensaje("No existe persona a registrar"), HttpStatus.CONFLICT);
			
			String token = Utils.sha256(persona.getEmail());
	
			Usuario usuario = new Usuario();
			
			// usuario.setClaveUsuario(oldPersona.getClaveUsuarioRegistro());
			usuario.setClaveRol(2);
			usuario.setNoControl(oldPersona.getNoControl());
			usuario.setLogin(persona.getUsuario().getLogin());
			usuario.setPasswd(Hashing.sha256().hashString(persona.getUsuario().getPasswd(), Charsets.UTF_8).toString());
			usuario.setToken(token);
			usuario.setFechaRegistro(new Timestamp(new Date().getTime()));
			usuario.setEstatus(-1);
			usuario.setNoAfiliacion(oldPersona.getNoPreAfiliacion());
			
			// usuario.setNoAfiliacion(oldPersona.getNoPreAfiliacion());
					
			if(usuarioDB.insertar(oldPersona, usuario, claveParentesco) == 1) {
				//if (Utils.loadPropertie("ambiente").equals(PRODUCCION) || Utils.loadPropertie("ambiente").equals(PRUEBAS)){
    		     //   mailService.prepareAndSendBienvenida(persona.getEmail(),persona.getNombreCompleto() ,persona.getEmail(),persona.gettUsuario().getToken(),persona.gettUsuario().getId());
    		    //}else{
    		        //Manda al correo de fdsditco@gmail.com
				
					oldPersona.setEmail(persona.getEmail());
					
					
				
					personaDB.actualiza(oldPersona);
				
					//oldPersona.setUsuario(usuarioDB.getUsuarioById(oldPersona.getNoControl()));
				
    		         mailService.prepareAndSendBienvenida("issstepregistro@gmail.com", oldPersona.getNombreCompleto() ,
    		        		  oldPersona.getEmail(), usuario.getToken(), oldPersona.getNoControl());
    		     //}	

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
	
	public ResponseEntity<?> activarRegistro(String token){
		try{	
			Usuario usuario = usuarioDB.getUsuarioByToken(token);
			if(usuario != null){
				if(usuario.getEstatus() <= 0){
					
					usuario.setEstatus(1);
					usuario.setFechaUltimoAcceso(new Timestamp(new Date().getTime()));
					usuario.setToken(null);
					usuarioDB.actualiza(usuario);
					
					return new ResponseEntity<>(new Mensaje("Usuario activado correctamente"), HttpStatus.OK);
					
				}else{
					return new ResponseEntity<>(new Mensaje("Usuario previamente activo"), HttpStatus.CONFLICT);
				}	
			}else{
				return new ResponseEntity<>(new Mensaje("Token invalido"), HttpStatus.CONFLICT);
			}
		}catch (Exception ex){
			ex.printStackTrace();
			System.err.println("Exception activarRegistro");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public ResponseEntity<?> registraDerechohabiente(long claveParentesco, Derechohabiente derechohabiente) {
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
			long estatusRegistro = personaDB.createDerechohabiente(claveParentesco, derechohabiente);
			
			if (estatusRegistro == 0) {
				System.out.println("No registrado");
				return new ResponseEntity<>(new Mensaje("No fue posible registrar al derechohabiente"), HttpStatus.CONFLICT);
			}
			
			if (estatusRegistro == -1) {
				System.out.println("Integridad");
				return new ResponseEntity<>(new Mensaje("No de control y no de pre-aficiliacion duplicados"), HttpStatus.CONFLICT);
			}
			
			return registraUsuario( false, derechohabiente, claveParentesco );
			
			// return new ResponseEntity<>(derechohabiente, HttpStatus.CREATED);					
			
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
	
}