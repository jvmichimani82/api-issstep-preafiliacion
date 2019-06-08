package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import issstep.afiliacion.db.PersonaDB;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Persona;

@Service
public class PersonaService {
	private static final Logger logger = LoggerFactory.getLogger(PersonaService.class);	
	
	@Autowired
	PersonaDB personaDB;
		
	public ResponseEntity<?> getPersonaByCurp(String curp) {
		Persona persona =  personaDB.getPersonaByCurp(curp);
	
		if (persona != null)
			return new ResponseEntity<>(persona, HttpStatus.OK);
		
		else
			return new ResponseEntity<>(new Mensaje("No existe persona con esa curp"), HttpStatus.CONFLICT);
		
    }
}
