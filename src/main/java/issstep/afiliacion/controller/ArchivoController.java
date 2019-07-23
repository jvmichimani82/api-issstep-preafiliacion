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
	 @RequestMapping(value="/uploadDocto/{noControl}/{noPreafiliacion}/{claveParentesco}/{claveTipoArchivo}", method = RequestMethod.POST, consumes="multipart/form-data")
	 public ResponseEntity<?> uploadDoctos(	@PathVariable("noControl") long noControl, 
			 								@PathVariable("noPreafiliacion") long noPreafiliacion, 
			 								@PathVariable("claveParentesco") long claveParentesco, 
			 								@PathVariable("claveTipoArchivo") long claveTipoArchivo, 
			 								@RequestParam("file") MultipartFile uploadingFiles, HttpServletResponse response) throws Exception {	       
	 	return archivoService.uploadDocumento(noControl, noPreafiliacion, claveParentesco, claveTipoArchivo, uploadingFiles, response );		
	 }
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/downloadDocto/{claveDocumento}", method = RequestMethod.GET)
		public ResponseEntity<?> downloadDocto(	@PathVariable("claveDocumento") long claveDocumento, 
												HttpServletResponse response) throws Exception {   
	 		return archivoService.dowloadDocumento(claveDocumento, response );	
	}
	
	//@JsonView(Archivo.Views.Simple.class)
	/* @RequestMapping(value="/listadoByidUsuario/{idUsuario}", method = RequestMethod.GET)
		public ResponseEntity<?> listaDoctos(@PathVariable("idUsuario") long idUsuario, HttpServletResponse response) throws Exception {   
	 		return archivoService.listaDocumentos(idUsuario, response );	
	}*/
	
	@RequestMapping(value="/deleteDocto/{claveDocumento}", method = RequestMethod.DELETE)
		public ResponseEntity<?> deleteDocumento(@PathVariable("claveDocumento") long claveDocumento, 
												 HttpServletResponse response) throws Exception {   
	 		return archivoService.deleteDocumento(claveDocumento, response );	
	}
	
	
	//@JsonView(Archivo.Views.Simple.class)
	@RequestMapping(value="/listadoByidParentesco/{idParentesco}", method = RequestMethod.GET)
		public ResponseEntity<?> listaDoctos(@PathVariable("idParentesco") long idParentesco, HttpServletResponse response) throws Exception {   
	 		return archivoService.listaDocumentos( idParentesco, response );	
	}
	
	//@JsonView(Archivo.Views.Simple.class)
		@RequestMapping(value="/listadoArchivos/{noControl}/{noPreAfiliacion}/{claveParentesco}", method = RequestMethod.GET)
			public ResponseEntity<?> listaArchivos(	@PathVariable("noControl") long noControl, 
													@PathVariable("noPreAfiliacion") long noPreAfiliacion,
													@PathVariable("claveParentesco") long claveParentesco,  
													HttpServletResponse response) throws Exception {   
		 		return archivoService.listaArchivos( noControl, noPreAfiliacion, claveParentesco, response );	
		}

}

