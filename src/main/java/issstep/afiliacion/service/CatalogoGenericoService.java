package issstep.afiliacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import issstep.afiliacion.db.CatalogoGenericoDB;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;
import issstep.afiliacion.model.Mensaje;

@Service
public class CatalogoGenericoService {
	
	@Autowired
	CatalogoGenericoDB catalogoGenericoDB;
		
	public ResponseEntity<?> getRegisters( String catalogo ) {	
		List<CatalogoGenerico> registros = catalogoGenericoDB.getRegistros(catalogo);
		
		if (registros == null)
			return new ResponseEntity<>(new Mensaje("No existe el catalogo: " + catalogo), HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<>(registros, HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getRegister( String catalogo, long id ) {
		List<CatalogoGenerico> registros = catalogoGenericoDB.getRegistros(catalogo);
		
		if (registros == null)
			return new ResponseEntity<>(new Mensaje("No existe el catalogo: " + catalogo), HttpStatus.NOT_FOUND);
		
		CatalogoGenerico catalogoGenerico = catalogoGenericoDB.getRegistro(catalogo, id);
		
		if (catalogoGenerico != null) 
			return new ResponseEntity<>(catalogoGenerico, HttpStatus.OK);		
		else
			return new ResponseEntity<>(new Mensaje("No existe registro con el id: " + id), HttpStatus.NO_CONTENT);
    }
	
	public ResponseEntity<?> updateRegister( String catalogo, CatalogoGenerico catalogoGenerico) {	
		CatalogoGenerico registro = catalogoGenericoDB.getRegistro(catalogo, catalogoGenerico.getId());
		
		if (registro != null) {		
			int idRegistro = catalogoGenericoDB.updateRegistro(catalogo, catalogoGenerico);
			
			if (idRegistro != 0) 
				return new ResponseEntity<>(catalogoGenerico , HttpStatus.OK);	
			else 
				return new ResponseEntity<>(new Mensaje("No se pudo actualizar el registro con id: " + catalogoGenerico.getId()), HttpStatus.SERVICE_UNAVAILABLE);
		}
		else
			return new ResponseEntity<>(new Mensaje("No existe registro con el id: " + catalogoGenerico.getId()), HttpStatus.CONFLICT);
    }
	
	public ResponseEntity<?> createRegister( String catalogo, Descripcion descripcion ) {	
		long idRegistro = catalogoGenericoDB.createRegistro(catalogo, descripcion);
		
		if (idRegistro != 0) {
			Documento documento= new Documento();
			
			documento.setDescripcion(descripcion.getDescripcion());
			documento.setId(idRegistro);
			
			return new ResponseEntity<>(documento , HttpStatus.CREATED);					
		}
		else
			return new ResponseEntity<>(new Mensaje("El registro no se pudo crear."), HttpStatus.SERVICE_UNAVAILABLE);
    }
	
	public ResponseEntity<?> deleteRegister( String catalogo, long id ) {
		CatalogoGenerico registro = catalogoGenericoDB.getRegistro(catalogo, id);
		
		if (registro != null) {	
			catalogoGenericoDB.deleteRegistro(catalogo, id);
			return new ResponseEntity<>( HttpStatus.OK);					
		}
		else
			return new ResponseEntity<>(new Mensaje("El registro con id: " + id + ", no existe."), HttpStatus.NOT_FOUND);
	}
}
