package issstep.afiliacion.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import issstep.afiliacion.cons.CatalogosCONST;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.service.CatalogoGenericoService;

@RestController
@RequestMapping("/catalogo")
public class CatalogoGenericoController {
	
	@Autowired
	CatalogoGenericoService catalogoGenericoService;
	
	 @RequestMapping(value = "/{catalogo}/{id}", method = RequestMethod.GET)
	    public ResponseEntity<?> obtenRegistro(@PathVariable("catalogo") String catalogo, @PathVariable("id") long id, HttpServletResponse response ) {
	    	return catalogoGenericoService.getRegister(catalogo, id);
	    }
	    
	    @RequestMapping(value = "/{catalogo}/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<?> eliminaRegistro(@PathVariable("catalogo") String catalogo, @PathVariable("id") long id, HttpServletResponse response ) {
	    	return catalogoGenericoService.deleteRegister(catalogo, id);
	    }
	    
	    @RequestMapping(value = "/{catalogo}", method = RequestMethod.GET)
	    public ResponseEntity<?> obtenRegistros(@PathVariable("catalogo") String catalogo, HttpServletResponse response ) {
	    	return catalogoGenericoService.getRegisters(catalogo);
	    }
	    
	    @RequestMapping(value = "/{catalogo}", method = RequestMethod.PUT)
	    public ResponseEntity<?> actualizaRegistro(@PathVariable("catalogo") String catalogo, 
	    											@ApiParam(value = CatalogosCONST.actualizacionGenerico, required = true)
	    											@RequestBody CatalogoGenerico catalogoGenerico, HttpServletResponse response ) {
	    	return catalogoGenericoService.updateRegister(catalogo, catalogoGenerico);
	    }
	    
	    @RequestMapping(value = "/{catalogo}", method = RequestMethod.POST)
	    public ResponseEntity<?> creaRegistro(@PathVariable("catalogo") String catalogo, @ApiParam(value = CatalogosCONST.registroGenerico, required = true)@RequestBody Descripcion descripcion, HttpServletResponse response ) {
	    	return catalogoGenericoService.createRegister(catalogo, descripcion);
	    }
}
