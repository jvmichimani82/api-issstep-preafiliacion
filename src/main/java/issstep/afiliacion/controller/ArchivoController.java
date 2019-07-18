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
	 @RequestMapping(value="/uploadDocto/{idTrabajador}/{idBeneficiario}/{idParentesco}/{tipoDocto}", method = RequestMethod.POST, consumes="multipart/form-data")
	 public ResponseEntity<?> uploadDoctos(	@PathVariable("idTrabajador") long idTrabajador, 
			 								@PathVariable("idBeneficiario") long idBeneficiario, 
			 								@PathVariable("idParentesco") long idParentesco, 
			 								@PathVariable("tipoDocto") long tipoDocto, 
			 								@RequestParam("file") MultipartFile uploadingFiles, HttpServletResponse response) throws Exception {	       
	 	return archivoService.uploadDocumento(idTrabajador, idBeneficiario, idParentesco, tipoDocto, uploadingFiles, response );		
	 }
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/downloadDocto/{idTrabajador}/{idBeneficiario}/{idParentesco}/{tipoDocto}", method = RequestMethod.GET)
		public ResponseEntity<?> bajaDoto(	@PathVariable("idTrabajador") long idTrabajador, 
											@PathVariable("idBeneficiario") long idBeneficiario, 
											@PathVariable("idParentesco") long idParentesco, 
											@PathVariable("tipoDocto") long tipoDocto,  
											HttpServletResponse response) throws Exception {   
	 		return archivoService.dowloadDocumento(idTrabajador, idBeneficiario, idParentesco, tipoDocto, response );	
	}
	
	//@JsonView(Archivo.Views.Simple.class)
	/* @RequestMapping(value="/listadoByidUsuario/{idUsuario}", method = RequestMethod.GET)
		public ResponseEntity<?> listaDoctos(@PathVariable("idUsuario") long idUsuario, HttpServletResponse response) throws Exception {   
	 		return archivoService.listaDocumentos(idUsuario, response );	
	}*/
	
	@RequestMapping(value="/deleteDocto/{idTrabajador}/{idBeneficiario}/{idParentesco}/{tipoDocto}", method = RequestMethod.DELETE)
		public ResponseEntity<?> deletefoto(@PathVariable("idTrabajador") long idTrabajador, 
											@PathVariable("idBeneficiario") long idBeneficiario, 
											@PathVariable("idParentesco") long idParentesco, 
											@PathVariable("tipoDocto") long tipoDocto,
											HttpServletResponse response) throws Exception {   
	 		return archivoService.deleteDocumento(idTrabajador, idBeneficiario, idParentesco, tipoDocto, response );	
	}
	
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/listadoByidParentesco/{idParentesco}", method = RequestMethod.GET)
		public ResponseEntity<?> listaDoctos(@PathVariable("idParentesco") long idParentesco, HttpServletResponse response) throws Exception {   
	 		return archivoService.listaDocumentos( idParentesco, response );	
	}
	
	//@JsonView(Archivo.Views.Simple.class)
		@RequestMapping(value="/listadoArchivos/{idTrabajador}/{idBeneficiario}/{idParentesco}", method = RequestMethod.GET)
			public ResponseEntity<?> listaArchivos(@PathVariable("idTrabajador") long idTrabajador, 
												 @PathVariable("idBeneficiario") long idBeneficiario, 
												 @PathVariable("idParentesco") long idParentesco,  
												 HttpServletResponse response) throws Exception {   
		 		return archivoService.listaArchivos( idTrabajador, idBeneficiario, idParentesco, response );	
		}

}

