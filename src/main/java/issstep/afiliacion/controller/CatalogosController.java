package issstep.afiliacion.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import issstep.afiliacion.cons.CatalogosCONST;
import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;
import issstep.afiliacion.service.CatalogosService;

@RestController
@RequestMapping("/catalogo")
public class CatalogosController {


    @Autowired
    public CatalogosService catalogosService;
    
    @RequestMapping(value = "/documento/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> obtenDocumento(@PathVariable("id") long id, HttpServletResponse response ) {
    	return catalogosService.getDocument(id);
    }
    
    @RequestMapping(value = "/documento/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminaDocumento(@PathVariable("id") long id, HttpServletResponse response ) {
    	return catalogosService.deleteDocument(id);
    }
    
    @RequestMapping(value = "/documento", method = RequestMethod.GET)
    public ResponseEntity<?> obtenDocumentos( HttpServletResponse response ) {
    	return catalogosService.getDocuments();
    }
    
    @RequestMapping(value = "/documento", method = RequestMethod.PUT)
    public ResponseEntity<?> actualizaDocumento(@ApiParam(value = CatalogosCONST.actualizacionDocumento, required = true)@RequestBody Documento documento, HttpServletResponse response ) {
    	return catalogosService.updateDocument(documento);
    }
    
    @RequestMapping(value = "/documento", method = RequestMethod.POST)
    public ResponseEntity<?> creaDocumento(@ApiParam(value = CatalogosCONST.registroDocumento, required = true)@RequestBody Descripcion descripcion, HttpServletResponse response ) {
    	return catalogosService.createDocument(descripcion);
    }
    
    @ApiOperation(value = "Catálogo de municipios de un estado")
    @RequestMapping(value = "/municipios/{idEstado}", method = RequestMethod.GET)
    public ResponseEntity<?> getMunicipios(@ApiParam(value = "idEstado", required = true) @PathVariable long idEstado, HttpServletResponse response ) {
    	return catalogosService.getMunicipios(idEstado);
    }
    
    @ApiOperation(value = "Catálogo de localidades de un municipio")
    @RequestMapping(value = "/localidades/{idEstado}/{idMunicipio}", method = RequestMethod.GET)
    public ResponseEntity<?> getLocalidades(@ApiParam(value = "idEstado", required = true) @PathVariable long idEstado,
    										@ApiParam(value = "idMunicipio", required = true) @PathVariable long idMunicipio,
    										HttpServletResponse response ) {
    	return catalogosService.getLocalidades(idEstado, idMunicipio);
    }
    
    @ApiOperation(value = "Catálogo de colonias de una localidad")
    @RequestMapping(value = "/colonias/{idEstado}/{idMunicipio}/{idLocalidad}", method = RequestMethod.GET)
    public ResponseEntity<?> getLocalidades(@ApiParam(value = "idEstado", required = true) @PathVariable long idEstado,
    										@ApiParam(value = "idMunicipio", required = true) @PathVariable long idMunicipio,
    										@ApiParam(value = "idLocalidad", required = true) @PathVariable long idLocalidad,
    										HttpServletResponse response ) {
    	return catalogosService.getColonias(idEstado, idMunicipio, idLocalidad);
    }
    
    @ApiOperation(value = "Catálogo de clínicas de servicio")
    @RequestMapping(value = "/clinicas", method = RequestMethod.GET)
    public ResponseEntity<?> getClinicas(HttpServletResponse response ) {
    	return catalogosService.getClinicas();
    }
}