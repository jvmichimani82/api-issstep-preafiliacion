package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import issstep.afiliacion.cons.DerechohabienteCONST;
import issstep.afiliacion.model.Curp;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Email;
import issstep.afiliacion.model.ResetPassword;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.service.DerechohabienteService;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/derechohabiente")
public class DerechohabienteController {


    @Autowired
    public DerechohabienteService derechohabienteService;
    
    //servicio para la validacion por curp de los derechohabientes
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/validadCurp", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaCurp( @ApiParam(value = DerechohabienteCONST.curp, required = true)@RequestBody Curp curp, HttpServletResponse response) {

    	return derechohabienteService.getPersonaByCurp(curp.getCurp());
    }
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/validadNombre", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaNombre( @ApiParam(value = DerechohabienteCONST.buscaDerechohabiente, required = true)  @RequestBody Derechohabiente persona, HttpServletResponse response) {

    	return derechohabienteService.getPersonaByNombre(persona, response);
    }
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/{idPersona}", method = RequestMethod.GET)
    public ResponseEntity<?> getPersonaById(@ApiParam(value = "idPersona", required = true) @PathVariable long idPersona, HttpServletResponse response) {

    	return derechohabienteService.getPersonaById(idPersona);
    }
    
    @ApiOperation(value = "Relación de beneficiarios de un derechohabiente")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/beneficiarios/{claveUsuarioRegistro}", method = RequestMethod.GET)
    public ResponseEntity<?> getBeneficiarios(@PathVariable("claveUsuarioRegistro") long claveUsuarioRegistro, HttpServletResponse response) {
    	
    	return derechohabienteService.getBeneficiarios(false, claveUsuarioRegistro);
    }
    
    @ApiOperation(value = "Relación de un derechohabiente y de sus beneficiarios ")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/beneficiariosConTitular/{claveUsuarioRegistro}", method = RequestMethod.GET)
    public ResponseEntity<?> getBeneficiariosConTitular(@PathVariable("claveUsuarioRegistro") long claveUsuarioRegistro, HttpServletResponse response) {
    	
    	return derechohabienteService.getBeneficiarios(true, claveUsuarioRegistro);
    }
    
    
    @ApiOperation(value = "Registro de Trabajadores en linea")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/registro/online", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registroOnline(@ApiParam(value = DerechohabienteCONST.registroUsuario, required = true) @RequestBody Derechohabiente persona) {
 
    	return derechohabienteService.registraUsuario(true, persona, 0);
    }
    
    @RequestMapping(value="/activar/{token}", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activarRegistro(@ApiParam(value = "Token", required = true) @PathVariable String token){

    	return derechohabienteService.activarRegistro(token);
	}
    
    @ApiOperation(value = "Registro de derechohabiente")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public ResponseEntity<?> registraDerechohabiente(@ApiParam(value = DerechohabienteCONST.registroDerechohabiente, required = true)@RequestBody Derechohabiente derechohabiente,
    												 HttpServletResponse response) {
   
    	return derechohabienteService.registraDerechohabiente(derechohabiente);
    }
    
    @ApiOperation(value = "Asignar beneficiario")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/asignar/beneficiario", method = RequestMethod.POST)
    public ResponseEntity<?> asignarBeneficiario(@ApiParam(value = DerechohabienteCONST.asignaBeneficiario, required = true)@RequestBody Beneficiario beneficiario,
    											 HttpServletResponse response) {
   
    	return derechohabienteService.asignarBeneficiario( beneficiario );
    }
    
    @ApiOperation(value = "Resetear password")
    @RequestMapping(value = "/recuperar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recuperarPassword(@ApiParam(value = DerechohabienteCONST.recuperarPassword, required = true)@RequestBody ResetPassword resetPassword, HttpServletResponse response ){

    	return derechohabienteService.recuperarPassword(resetPassword);
	}
    
    @ApiOperation(value = "Solicitud para resetear password")
    @RequestMapping(value = "/solicitud/recuperar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recuperarPassword( @ApiParam(value = DerechohabienteCONST.email, required = true)@RequestBody Email email, HttpServletResponse response ){

    	return derechohabienteService.solicitudRecuperarPassword(email.getEmail());
	}
    
    @ApiOperation(value = "Actualizar password")
    @RequestMapping(value = "/actualizar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recuperarPassword( @ApiParam(value = DerechohabienteCONST.actualizarPassword, required = true)@RequestBody ActualizarPassword actualizarPassword, HttpServletResponse response ){

    	return derechohabienteService.actualizarPassword(actualizarPassword);
	}
    
    @ApiOperation(value = "Actualizar datos")
    @RequestMapping(value = "/actualizar/datos", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> actualizarDatos( @ApiParam(value = DerechohabienteCONST.actualizarDatos, required = true)@RequestBody ActualizarDatos actualizarDireccion, HttpServletResponse response ){

    	return derechohabienteService.actualizarDatos(actualizarDireccion);
	}
    
    @ApiOperation(value = "Relación de derechohabientes por estatus de validación")
    @RequestMapping(value = "/relacion/{estatusValidacion}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesPorEstatusDeValidacion(@ApiParam(value = "2 - Por validar, \n 1 - Valido, \n  0 - Invalido", required = true)
	 																		@PathVariable("estatusValidacion") int estatusValidacion,  
	 																		HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesPorEstatusDeValidacion(estatusValidacion);
	}
    
    
}