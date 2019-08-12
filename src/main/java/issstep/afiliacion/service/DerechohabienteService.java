package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

// import org.json.JSONObject;
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
import issstep.afiliacion.model.NumerosParaRegistro;
import issstep.afiliacion.model.ResetPassword;
import issstep.afiliacion.model.ResultadoBusqueda;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.DocumentosFaltantes;
import issstep.afiliacion.model.InfoDerechohabiente;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.DatoABuscar;
import issstep.afiliacion.model.ResultadoValidacion;
import issstep.afiliacion.model.Colonia;
import issstep.afiliacion.utils.Utils;

@Service
public class DerechohabienteService {
	private static final Logger logger = LoggerFactory.getLogger(DerechohabienteService.class);	
	
	// private static final String expRegParaNumero = "^[0-9]+";
	// private static final String expRegParaCURP = "^([A-Za-z]{1}[AEIOUaeiou]{1}[A-Za-z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HMhm]{1}([A|a][S|s]|[B|b][C|c]|[B|b][S|s]|[C|c][C|c]|[C|c][S|s]|[C|c][H|h]|[C|c][L|l]|[C|c][M|m]|[D|d][F|f]|[D|d][G|g]|[G|g][T|t]|[G|g][R|r]|[H|h][G|g]|[J|j][C|c]|[M|m][C|c]|[M|m][N|n]|[M|m][S|s]|[N|n][T|t]|[N|n][L|l]|[O|o][C|c]|[P|p][L|l]|[Q|q][T|t]|[Q|q][R|r]|[S|s][P|p]|[S|s][L|l]|[S|s][R|r]|[T|t][C|c]|[T|t][S|s]|[T|t][L|l]|[V|v][Z|z]|[Y|y][N|n]|[Z|z][S|s]|[N|n][E|e])[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]{3}[0-9A-Za-z]{2})$";
	// private static final String expRegPatronCURP = "^([A-Z]{4}[0-9]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{6}[A-Z0-9]{1,2})$";
	
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
			return null;
		
		fillDerechohabiente(persona);
		return new ResponseEntity<>(persona, HttpStatus.OK);		
    }
	
	public ResponseEntity<?> validaPersonaNoAfiliacion(long noAfiliacion) {
		ResponseEntity<?> persona = getPersonaByNoControlNoPreafiliacion(noAfiliacion, noAfiliacion);
		
		if (persona.getStatusCodeValue() == 200) 
			return new ResponseEntity<>(new Mensaje("Ya existe una cuenta con ese numero de afiliacion"), HttpStatus.CONFLICT);
			
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
	
	
	public ResponseEntity<?> getPersonaByNoControlNoPreafiliacion(long noControl, long noPreAfiliacion) {
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(noControl, noPreAfiliacion);
			
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
		try{
			Derechohabiente oldPersona = null;
			
			// Revisamos si el registro trae curp sino haremos la consulta por noControl y NoAfiliacion
			if(persona.getCurp() != null) {
				
				if (!Utils.esCURP(persona.getCurp()))
					return new ResponseEntity<>(new Mensaje("Formato de CURP invalido"), HttpStatus.BAD_REQUEST);
				
				// Revisamos que la persona exista en nuestra base de datos
				 oldPersona =  personaDB.getPersonaByColumnaStringValor("CURP", persona.getCurp());
				
				// Sin encontramos un resultado en nuestra bd rechacamos la creacion del usuario
				if(oldPersona != null)
					return new ResponseEntity<>(new Mensaje("Se encontro un registro en nuestra bd"), HttpStatus.CONFLICT);
				
				// Sino hacemos la consulta a la bd de issstep
				oldPersona = personaDB.getTrabajadorIssstepByColumnaStringValor("CURP", persona.getCurp());
					
				//Si encontramos un resultado en la base de datos ISSSTEP procedemos a la creacion del Derechohabiente y su usuario de la plataforma
				if(oldPersona != null) {
					int estatus = -1;
					if (persona.getEmail().equals("issstepregistro@gmail.com")) {
						oldPersona.setEmail("issstepregistro" + oldPersona.getNoControl() +  "@gmail.com");
						persona.setEmail("issstepregistro" + oldPersona.getNoControl() +  "@gmail.com");
						estatus = 1;
					}
				
					if(personaDB.createDerechohabiente(oldPersona, false, 4) > 0) { 
						// Benefiario del trabajador
						beneficiarioDB.createBeneficiario(oldPersona.getNoControl(),  oldPersona, 0);
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
		        //Manda al correo de fdsditco@gmail.com
			
			    oldPersona.setEmail(newPersona.getEmail());
				
				
			    oldPersona.setClaveUsuarioRegistro(claveUsuario);
				personaDB.actualiza(oldPersona);
				
				// if (!oldPersona.getEmail().equals("issstepregistro@gmail.com" + oldPersona.getNoControl() + "@gmail.com"))	
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
	
	public ResponseEntity<?> registraDerechohabiente( Derechohabiente registroDerechohabiente ) {
		ResultadoValidacion resultadoValidacion =  validaDatosRegistro(registroDerechohabiente);
		
		if  (getPersonaByCurp(registroDerechohabiente.getCurp()) != null) 
			return new ResponseEntity<>(new Mensaje("Ya exite un derechohabiente con esa Curp"), HttpStatus.CONFLICT);
		
		if (!resultadoValidacion.isEtatus())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
		
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
		Derechohabiente derechohabienteTitular = null;
		
		if (usuario == null)
			return new ResponseEntity<>(new Mensaje("Usuario no logeado"), HttpStatus.BAD_REQUEST);
		
		
		boolean esAdmin = usuario.getClaveRol() == 1;
		
		if (!esAdmin) 
			if (registroDerechohabiente.getClaveParentesco() == 0)
				return new ResponseEntity<>(new Mensaje("Usuario no puede dar de alta a un titular"), HttpStatus.BAD_REQUEST);
				
		try{	
			
			if (registroDerechohabiente.getClaveParentesco() == 0) {
				derechohabienteTitular = new Derechohabiente();
				derechohabienteTitular.setNoControl(0);
			}
			else
			{
				derechohabienteTitular = personaDB.getPersonaByNoControlNoPreafiliacion(
									usuario.getNoControl(), 
									usuario.getNoControl());
				
				if (derechohabienteTitular == null)
					return new ResponseEntity<>(new Mensaje("No existe el regitro del titular"), HttpStatus.BAD_REQUEST);	
				
				registroDerechohabiente.setNoControl(usuario.getNoControl()); 
				inicializaConValoresDelTitular( registroDerechohabiente, derechohabienteTitular);			
			}
			
			String[] fechaNac = registroDerechohabiente.getFechaNacimiento().split("/");
			registroDerechohabiente.setFechaNacimiento(fechaNac[2] + "/" + fechaNac[1] + "/"+ fechaNac[0]);
			
			NumerosParaRegistro numerosParaRegistro = personaDB.getNextNumerosRegistro(registroDerechohabiente.getClaveParentesco(), registroDerechohabiente.getNoControl());
			
			registroDerechohabiente.setNoControl(numerosParaRegistro.getNoControl());
			registroDerechohabiente.setNoPreAfiliacion(numerosParaRegistro.getNoAfiliacion());
			registroDerechohabiente.setFechaRegistro(new Timestamp(new Date().getTime()));
			registroDerechohabiente.setSituacion(1);
			registroDerechohabiente.setClaveUsuarioRegistro(usuario.getClaveUsuario());
			registroDerechohabiente.setClaveUsuarioModificacion(usuario.getClaveUsuario());
					
			long estatusRegistro = personaDB.createDerechohabiente( registroDerechohabiente, esAdmin, 1);
			
			if (estatusRegistro == 0) 
				return new ResponseEntity<>(new Mensaje("No fue posible registrar al derechohabiente"), HttpStatus.INTERNAL_SERVER_ERROR);
				
			
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
	
	void inicializaConValoresDelTitular(Derechohabiente registroDerechohabiente, Derechohabiente derechohabienteTitular) {
		
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
	}
	
	ResultadoValidacion validaDatosRegistro(Derechohabiente datosRegistro) {
		ResultadoValidacion resultadoValidacion =  new ResultadoValidacion();
		resultadoValidacion.setEtatus(true);
				
		if (datosRegistro.getClaveParentesco() != 0 && datosRegistro.getNoControl() == 0) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el numero de control");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getCurp() == null) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo curp");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getRfc() == null) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo rfc");
			return resultadoValidacion;
		}
		
		if (datosRegistro.getClaveParentesco() == 0) {
			if (datosRegistro.getDireccion() == null) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo direccion");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getTelefonoCasa() == null) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCasa");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getTelefonoCelular() == null) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCelular");
				return resultadoValidacion;
			}
						
			if (datosRegistro.getClaveClinicaServicio() == 0) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveClinicaServicio");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveColonia() == 0) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveColonia");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveEstado() == 0) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveEstado");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveLocalidad() == 0) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveLocalidad");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getClaveMunicipio() == 0) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveMunicipio");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getCodigoPostal() == null) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo claveCodigoPostal");
				return resultadoValidacion;
			}
			
			if (datosRegistro.getFechaPreAfiliacion() == null) {
				resultadoValidacion.setEtatus(false);
				resultadoValidacion.setMensaje("Debe proporcional el campo fechaPreAfiliacion");
				return resultadoValidacion;
			}
		}
		
		if (datosRegistro.getClaveEstadoCivil() == 0) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo claveEstadoCivil");
			return resultadoValidacion;
		}
		
		return resultadoValidacion;
		
	}
	
	public ResponseEntity<?> asignarBeneficiario(Beneficiario beneficiario) {
		
		CatalogoGenerico catalogoGenerico = catalogoGenericoDB.getRegistro("WKPARENTESCO", beneficiario.getClaveParentesco());
		
		if (catalogoGenerico == null)
			return new ResponseEntity<>(new Mensaje("Clave de parentesco invalida"), HttpStatus.BAD_REQUEST);
		
		if (beneficiario.getClaveParentesco() == 0)
			return new ResponseEntity<>(new Mensaje("No puede registrar un beneficiario con clave de titular"), HttpStatus.BAD_REQUEST);

		if (personaDB.existeBeneficiarioRegistradoById(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion()))
			return new ResponseEntity<>(new Mensaje("Ya existe un beneficiario registrado"), HttpStatus.BAD_REQUEST);
		
		if (beneficiario.getClaveParentesco() != 6 && beneficiario.getClaveParentesco() != 7) 
			if (personaDB.existeBeneficiarioRegistrado(beneficiario.getNoControl(), beneficiario.getClaveParentesco()))
				return new ResponseEntity<>(new Mensaje("Ya existe un beneficiario registrado con ese parenteso"), HttpStatus.BAD_REQUEST);	
		
		Derechohabiente persona =  personaDB.getPersonaByNoControlNoPreafiliacion(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion());
		
		// Sino tenemos resultado de nuestra base de datos vamos por los datos a la bd de issstep
		if(persona == null) {
			
			persona = personaDB.getPersonaByNoControlNoAfiliacionIssstep(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion());
			persona.setNoControl(beneficiario.getNoControlTitular());
			personaDB.createDerechohabiente(persona, false, 4);
		}
		
		Beneficiario oldBeneficiario = beneficiarioDB.getBeneficiario(beneficiario.getNoControl(), beneficiario.getNoPreAfiliacion(), beneficiario.getClaveParentesco());
		
		if (oldBeneficiario != null)
			return new ResponseEntity<>(new Mensaje("Ya existe el beneficiario"), HttpStatus.CONFLICT);
		
		try{
			
			persona.setSituacion(1);
			persona.setFechaRegistro(new Timestamp(new Date().getTime()));
				
			long noBeneficiario = beneficiarioDB.createBeneficiario(beneficiario.getNoControlTitular(), persona, beneficiario.getClaveParentesco());
			
			if (noBeneficiario == 0)
				return new ResponseEntity<>(new Mensaje("No fue posible asignar el beneficiario"), HttpStatus.INTERNAL_SERVER_ERROR);
			
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
		
		if (resultadoValidacion.isEtatus())
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
		resultadoValidacion.setEtatus(true);
		
		if (actualizarPassword.getPasswordActual() == null) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo passwordActual");
			return resultadoValidacion;
		}
		
		if (actualizarPassword.getPasswordNuevo() == null) {
			resultadoValidacion.setEtatus(false);
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
		
		if (!resultadoValidacion.isEtatus())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.BAD_REQUEST);
			
		Derechohabiente derechohabiente = personaDB.getPersonaByNoControlNoPreafiliacion(
														datosDerechohabiente.getNoControl(), 
														datosDerechohabiente.getNoPreAfiliacion());
		
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
		resultadoValidacion.setEtatus(true);
		
		if (actualizarDatos.getNoControl() == 0) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo noControl");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getNoPreAfiliacion() == 0) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo noPreAfiliacion");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getDireccion() == null) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo direccion");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getTelefonoCasa() == null) {
			resultadoValidacion.setEtatus(false);
			resultadoValidacion.setMensaje("Debe proporcional el campo telefonoCasa");
			return resultadoValidacion;
		}
		
		if (actualizarDatos.getTelefonoCelular() == null) {
			resultadoValidacion.setEtatus(false);
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
			
			Derechohabiente trabajador = null;
			
			trabajador = personaDB.getPersonaByNoControlNoAfiliacionIssstep(noControl, noControl);
			
			// Verificamos si se encontrol en la bd de derecohabientes
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
					return new ResponseEntity<>(new Mensaje("No fue posible registrar la información del trabajador para realizar el proceso de pre-afiliación: " + noControl), HttpStatus.OK);				
			}
			else 
				return new ResponseEntity<>(new Mensaje("No existe información para el número de control: " + noControl), HttpStatus.BAD_REQUEST);
		}
		
    }
	
	public ResponseEntity<?> getDocumentacionBeneficiarios(boolean incluirTitular, long noControl) {		
		List<DocumentosFaltantes> listaDocumentosFaltantes = personaDB.getDocumentacionByDerechohabiente(incluirTitular, noControl);
		
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
	
	public ResponseEntity<?> buscarInformacionEnPreafiliacion(boolean enPreafiliacion, DatoABuscar datoABuscar) {
		if (datoABuscar.getDato() == null)
			return new ResponseEntity<>(new Mensaje("Debe proporcional el campo dato"), HttpStatus.BAD_REQUEST);
		
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
			resultadoBusqueda = personaDB.getInformacionAfiliaconByCampo(campo, datoABuscar.getDato(), esValorNumerico);
		
		
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
		
		Derechohabiente afiliado =  personaDB.getPersonaByNoControlNoPreafiliacion( noControl, noPreAfiliacion );
		
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
				int numDocsBeneficiario = getNoDocumentosFaltantes(listaDocumentosFaltantes, noControl, noPreAfiliacion);
				if ( numDocsBeneficiario > 0) 
					return new ResponseEntity<>(new Mensaje("Aún tiene documentación pendiente"), HttpStatus.CONFLICT);
			// }
		}
		
		if(!usuarioDB.actualizaEstatusDerechohabiente(noControl, noPreAfiliacion, estatus))
			return new ResponseEntity<>(new Mensaje("No fue posible actualizar el estatus"), HttpStatus.INTERNAL_SERVER_ERROR);
		
		return new ResponseEntity<>(new Mensaje("Actualizacion exitosa"), HttpStatus.OK);
    }
	
	int getNoDocumentosFaltantes(List<DocumentosFaltantes> listaDocumentosFaltantes, long noControl, long noPreAfiliacion) {		
		int docNoValidos = 0;
		for(DocumentosFaltantes reg: listaDocumentosFaltantes) {
			if (reg.getNoControl() == noControl && reg.getNoPreAfiliacion() == noPreAfiliacion && reg.getEsValido() != 1) 
				docNoValidos += reg.getNumDocs();
		}
		
		return docNoValidos;
	}
}
