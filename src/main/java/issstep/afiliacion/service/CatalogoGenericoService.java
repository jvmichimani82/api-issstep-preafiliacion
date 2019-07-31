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
			return new ResponseEntity<>(new Mensaje("No existe registro con id: " + id), HttpStatus.NOT_FOUND);
    }
	
	public ResponseEntity<?> updateRegister( String catalogo, CatalogoGenerico catalogoGenerico) {	
		if (catalogoGenerico.getDescripcion() == null)
			return new ResponseEntity<>(new Mensaje("El campo descripcion es necesario"), HttpStatus.BAD_REQUEST);
		
		if (catalogoGenerico.getId() == 0)
			return new ResponseEntity<>(new Mensaje("El campo id es necesario "), HttpStatus.BAD_REQUEST);
		
		String nombreId = catalogoGenericoDB.getNombreId(catalogo);
		
		if (nombreId == "")
			return new ResponseEntity<>(new Mensaje("No existe el catalogo: " + catalogo), HttpStatus.NOT_FOUND);
		
		CatalogoGenerico registro = catalogoGenericoDB.getRegistro(catalogo, catalogoGenerico.getId());
		
		if (registro == null) 
			return new ResponseEntity<>(new Mensaje("No existe registro con el id: " + catalogoGenerico.getId()), HttpStatus.CONFLICT);			
			
		int idRegistro = catalogoGenericoDB.updateRegistro(catalogo, catalogoGenerico);
		
		if (idRegistro == 0) 
			return new ResponseEntity<>(new Mensaje("No se pudo actualizar el registro con id: " + catalogoGenerico.getId()), HttpStatus.INTERNAL_SERVER_ERROR);
						
		return new ResponseEntity<>(catalogoGenerico , HttpStatus.OK);			
    }
	
	public ResponseEntity<?> createRegister( String catalogo, Descripcion descripcion ) {	
		if (descripcion.getDescripcion() == null)
			return new ResponseEntity<>(new Mensaje("El campo descripcion es necesario"), HttpStatus.BAD_REQUEST);
		
		long idBusqueda = catalogoGenericoDB.getRegistroByDescripcion( catalogo, descripcion.getDescripcion() );
				
		if (idBusqueda == -1)
			return new ResponseEntity<>(new Mensaje("No existe el catalogo: " + catalogo), HttpStatus.NOT_FOUND);
		
		if (idBusqueda > 0 )
			return new ResponseEntity<>(new Mensaje("Ya existe un registro con esa descripcion"), HttpStatus.CONFLICT);
				
		long idRegistro = catalogoGenericoDB.createRegistro(catalogo, descripcion);		
					
		if (idRegistro == 0)
			return new ResponseEntity<>(new Mensaje("El registro no se pudo crear."), HttpStatus.INTERNAL_SERVER_ERROR);
		
		Documento documento= new Documento();
		
		documento.setDescripcion(descripcion.getDescripcion());
		documento.setId(idRegistro);
		
		return new ResponseEntity<>(documento , HttpStatus.CREATED);					
    }
	
	public ResponseEntity<?> deleteRegister( String catalogo, long id ) {
		String nombreId = catalogoGenericoDB.getNombreId(catalogo);
		
		if (nombreId == "")
			return new ResponseEntity<>(new Mensaje("No existe el catalogo: " + catalogo), HttpStatus.NOT_FOUND);
		
		CatalogoGenerico registro = catalogoGenericoDB.getRegistro(catalogo, id);
				
		if (registro == null)
			return new ResponseEntity<>(new Mensaje("El registro con id: " + id + ", no existe."), HttpStatus.NOT_FOUND);	
		
		catalogoGenericoDB.deleteRegistro(catalogo, id);
		return new ResponseEntity<>( HttpStatus.OK);					
		
	}
}
