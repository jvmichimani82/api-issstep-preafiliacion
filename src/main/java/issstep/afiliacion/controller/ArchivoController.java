package issstep.afiliacion.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import issstep.afiliacion.service.ArchivoService;

import javax.servlet.http.HttpServletResponse;
@RestController
@RequestMapping("/archivo")
public class ArchivoController {
	
	@Autowired
	ArchivoService archivoService;

	 //@JsonView(Archivo.Views.Simple.class)
	 @RequestMapping(value="/uploadDocto/{tipoDocto}/{idUsuario}", method = RequestMethod.POST, consumes="multipart/form-data")
	 public ResponseEntity<?> uploadDoctos(@PathVariable("tipoDocto") int tipoDocto, @PathVariable("idUsuario") long idUsuario, @RequestParam("file") MultipartFile uploadingFiles, HttpServletResponse response) throws Exception {	       
	 	return archivoService.uploadDocto(tipoDocto, idUsuario, uploadingFiles, response );		
	 }
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/downloadDocto/{idDocto}", method = RequestMethod.GET)
		public ResponseEntity<?> bajaDoto(@PathVariable("idDocto") long idDocto, HttpServletResponse response) throws Exception {   
	 		return archivoService.dowloadDocumento(idDocto, response );	
	}
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/lista/{idUsuario}", method = RequestMethod.GET)
		public ResponseEntity<?> listaDoctos(@PathVariable("idUsuario") long idUsuario, HttpServletResponse response) throws Exception {   
	 		return null;//archivoService.listaDocumento(idUsuario, response );	
	}
	
	/*@RequestMapping(value="/deleteDocto/{idDocto}", method = RequestMethod.DELETE)
		public ResponseEntity<?> deletefoto(@PathVariable("idDocto") long idDocto, HttpServletResponse response) throws Exception {   
	 		return archivoService.deleteDocumento(idDocto, response );	
	}*/
	

}

