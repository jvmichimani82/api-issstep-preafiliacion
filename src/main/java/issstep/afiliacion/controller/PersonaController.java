package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import issstep.afiliacion.model.Curp;
import issstep.afiliacion.service.PersonaService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/persona")
public class PersonaController {


    @Autowired
    public PersonaService personaService;
    
    @RequestMapping(value = "/validadCurp", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaCurp(@ModelAttribute Curp curp, HttpServletResponse response) {

    	
	  	return personaService.getPersonaByCurp(curp.getCurp());
    }
}