package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import issstep.afiliacion.service.DerechohabienteService;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/derechohabiente")
public class DerechohabienteController {


    @Autowired
    public DerechohabienteService personaService;
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/validadCurp", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaCurp( @ApiParam(value = DerechohabienteCONST.curp, required = true)@RequestBody Curp curp, HttpServletResponse response) {

    	return personaService.getPersonaByCurp(curp.getCurp());
    }
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/{idPersona}", method = RequestMethod.GET)
    public ResponseEntity<?> getPersonaById(@ApiParam(value = "idPersona", required = true) @PathVariable long idPersona, HttpServletResponse response) {

    	return personaService.getPersonaById(idPersona);
    }
    
    
    @ApiOperation(value = "Registro de Trabajadores en linea")
    @JsonView(Derechohabiente.Views.RegistroUsuario.class)
    @RequestMapping(value = "/registro/online", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registroOnline(@ApiParam(value = DerechohabienteCONST.registroUsuario, required = true) @RequestBody Derechohabiente persona) {
 
    	return personaService.registraUsuario(true, persona);
    }
    
    @RequestMapping(value="/activar/{token}", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activarRegistro(@ApiParam(value = "Token", required = true) @PathVariable String token){

    	return personaService.activarRegistro(token);
	}
    
    
    
}