package issstep.afiliacion.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import issstep.afiliacion.model.Comentario;
import issstep.afiliacion.service.ArchivoService;

import javax.servlet.http.HttpServletResponse;
@RestController
@RequestMapping("/archivo")
public class ArchivoController {
	
	@Autowired
	ArchivoService archivoService;

	 @ApiOperation(value = "Subir un documento")
	 @RequestMapping(value="/uploadDocto/{noControlTitular}/{noControl}/{noPreafiliacion}/{claveParentesco}/{claveTipoArchivo}", method = RequestMethod.POST, consumes="multipart/form-data")
	 public ResponseEntity<?> uploadDoctos(	@PathVariable("noControlTitular") long noControlTitular, 
			 								@PathVariable("noControl") long noControl,
			 								@PathVariable("noPreafiliacion") long noPreafiliacion, 
			 								@PathVariable("claveParentesco") long claveParentesco, 
			 								@PathVariable("claveTipoArchivo") long claveTipoArchivo, 
			 								@RequestParam("file") MultipartFile uploadingFiles, HttpServletResponse response) throws Exception {	       
	 	return archivoService.uploadDocumento(noControlTitular, noControl, noPreafiliacion, claveParentesco, claveTipoArchivo, uploadingFiles, response );		
	 }
	
	 @ApiOperation(value = "Actualizar un documento de un derechohabiente")
	 @RequestMapping(value="/updateDocto/{claveDocumento}", method = RequestMethod.PUT, consumes="multipart/form-data")
	 public ResponseEntity<?> updateDocto(	@PathVariable("claveDocumento") long claveDocumento, 
			 								@RequestParam("file") MultipartFile uploadingFiles, HttpServletResponse response) throws Exception {	       
	 	return archivoService.updateDocumento(claveDocumento, uploadingFiles, response );		
	 } 
	 
	 @ApiOperation(value = "Descargar un documento")
	 @RequestMapping(value="/downloadDocto/{claveDocumento}", method = RequestMethod.GET)
		public ResponseEntity<?> downloadDocto(	@PathVariable("claveDocumento") long claveDocumento, 
												HttpServletResponse response) throws Exception {   
	 		return archivoService.dowloadDocumento(claveDocumento, response );	
	 }
	 
	 @ApiOperation(value = "Descargar un QR")
	 @RequestMapping(value="/qr/{cadena}", method = RequestMethod.GET)
		public ResponseEntity<?> downloadDocto(	@PathVariable("cadena") String cadena, 
												HttpServletResponse response) throws Exception {   
	 		return archivoService.getQRCodeImage(cadena, 200,200 );	
	 }
	
	 @ApiOperation(value = "Eliminar un documento")
	 @RequestMapping(value="/deleteDocto/{claveDocumento}", method = RequestMethod.DELETE)
		public ResponseEntity<?> deleteDocumento(@PathVariable("claveDocumento") long claveDocumento, 
												 HttpServletResponse response) throws Exception {   
	 		return archivoService.deleteDocumento(claveDocumento, response );	
	 }
	
	
	 @ApiOperation(value = "Relacion de documentos necesarios por parentesco")
	 @RequestMapping(value="/listadoByidParentesco/{idParentesco}", method = RequestMethod.GET)
 		public ResponseEntity<?> listaDoctos(@PathVariable("idParentesco") long idParentesco, HttpServletResponse response) throws Exception {   
	 		return archivoService.listaDocumentos( idParentesco, response );	
 	 }
	
	 @ApiOperation(value = "Relacion de documentos de un derecohabiente")
	 @RequestMapping(value="/listadoArchivos/{noControlTitular}/{noControl}/{noPreAfiliacion}/{claveParentesco}", method = RequestMethod.GET)
		public ResponseEntity<?> listaArchivos(	@PathVariable("noControlTitular") long noControlTitular,
												@PathVariable("noControl") long noControl,
												@PathVariable("noPreAfiliacion") long noPreAfiliacion,
												@PathVariable("claveParentesco") long claveParentesco,  
												HttpServletResponse response) throws Exception {   
	 		return archivoService.listaArchivos( noControlTitular, noControl, noPreAfiliacion, claveParentesco, response );	
	 }
	 
     @ApiOperation(value = "Establece la valiacion de un documento")
	 @RequestMapping(value="/validacion/{claveDocumento}/{estatusValidacion}", method = RequestMethod.PUT)
 		public ResponseEntity<?> updateValidacionDocto( 
 											 @PathVariable("claveDocumento") long claveDocumento,
 											 @ApiParam(value = "1 - Valido, \n  0 - Invalido", required = true)
 											 @PathVariable("estatusValidacion") int estatusValidacion,
 											 @RequestBody (required=false) Comentario comentario,
 											 HttpServletResponse response) throws Exception {  
    	 
    	 String coment = comentario != null ? comentario.getComentario(): "";
    	 
    		 
	 		return archivoService.updateValidacionDocto( claveDocumento, estatusValidacion, coment, response );	
 	 }	 
}

