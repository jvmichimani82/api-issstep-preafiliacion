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

import issstep.afiliacion.db.TrabajadorDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.utils.Utils;



@Service
public class DerechohabienteService {
	private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
	@Autowired
	TrabajadorDB personaDB;
	
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
	
	
	public ResponseEntity<?> registraUsuario(boolean registroOnline, Derechohabiente persona){
		try{
			Derechohabiente oldPersona = personaDB.getPersonaByColumnaStringValor("CURP", persona.getCurp());
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
			
			usuario.setNoControl(oldPersona.getNoControl());
			usuario.setNoAfiliacion(oldPersona.getNoPreAfiliacion());
			usuario.setNoRol(2);
			//usuario.setLogin(persona.getUsuario().getLogin());
			//usuario.setPasswd(Hashing.sha256().hashString(persona.getUsuario().getPasswd(), Charsets.UTF_8).toString());
			usuario.setToken(token);
			usuario.setActivo(-1);
			usuario.setFechaRegistro(new Timestamp(new Date().getTime()));
					
			if(registroOnline && usuarioDB.insertar(usuario) == 1) {
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
	
	public ResponseEntity<?> activarRegistro(String token){
		try{	
			Usuario usuario = usuarioDB.getUsuarioByToken(token);
			if(usuario != null){
				if(usuario.getActivo() <= 0){
					
					usuario.setActivo(1);
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
	
	
}
