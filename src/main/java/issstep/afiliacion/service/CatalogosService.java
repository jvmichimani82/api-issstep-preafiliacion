package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import issstep.afiliacion.db.DocumentoDB;
import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;
import issstep.afiliacion.model.Mensaje;

@Service
public class CatalogosService {
	
	@Autowired
	DocumentoDB documentoDB;
		
	public ResponseEntity<?> getDocuments() {	
		return new ResponseEntity<>(documentoDB.getDocumentos(), HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getDocument(long id) {	
		Documento documento = documentoDB.getDocumento(id);
		
		if (documento != null) 
			return new ResponseEntity<>(documento, HttpStatus.OK);		
		else
			return new ResponseEntity<>(new Mensaje("No existe documento con el id: " + id), HttpStatus.NO_CONTENT);
    }
	
	public ResponseEntity<?> updateDocument(Documento documento) {	
		int numDocumento = documentoDB.updateDocumento(documento);
		
		if (numDocumento != 0) 
			return new ResponseEntity<>(documento , HttpStatus.OK);		
		else
			return new ResponseEntity<>(new Mensaje("No existe documento con el id: " + documento.getId()), HttpStatus.CONFLICT);
    }
	
	public ResponseEntity<?> createDocument(Descripcion descripcion) {	
		long idDocumento = documentoDB.createDocumento(descripcion);
		
		if (idDocumento != 0) {
			Documento documento= new Documento();
			
			documento.setDescripcion(descripcion.getDescripcion());
			documento.setId(idDocumento);
			
			return new ResponseEntity<>(documento , HttpStatus.CREATED);					
		}
		else
			return new ResponseEntity<>(new Mensaje("El documento no se pudo crear."), HttpStatus.SERVICE_UNAVAILABLE);
    }
	
	public ResponseEntity<?> deleteDocument(long id) {	
		long idDocumento = documentoDB.deleteDocumento(id);
		
		if (idDocumento != 0)
			return new ResponseEntity<>( HttpStatus.OK);					
		else
			return new ResponseEntity<>(new Mensaje("El documento con id: " + id + ", no existe."), HttpStatus.NOT_FOUND);
	}
}
