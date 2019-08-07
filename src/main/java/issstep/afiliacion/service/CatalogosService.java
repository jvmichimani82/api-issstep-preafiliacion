package issstep.afiliacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import issstep.afiliacion.db.CatalogoDB;
import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;
import issstep.afiliacion.model.Mensaje;

@Service
public class CatalogosService {
	
	@Autowired
	CatalogoDB catalogoDB;
		
	public ResponseEntity<?> getDocuments() {	
		return new ResponseEntity<>(catalogoDB.getDocumentos(), HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getDocument(long id) {	
		Documento documento = catalogoDB.getDocumento(id);
		
		if (documento != null) 
			return new ResponseEntity<>(documento, HttpStatus.OK);		
		else
			return new ResponseEntity<>(new Mensaje("No existe documento con el id: " + id), HttpStatus.NO_CONTENT);
    }
	
	public ResponseEntity<?> updateDocument(Documento documento) {	
		Documento registro = catalogoDB.getDocumento(documento.getId());
		
		if (registro != null) {
			int numDocumento = catalogoDB.updateDocumento(documento);
			
			if (numDocumento != 0) 
				return new ResponseEntity<>(documento , HttpStatus.OK);
			else 
				return new ResponseEntity<>(new Mensaje("No se pudo actualizar el registro con id: " + documento.getId()), HttpStatus.SERVICE_UNAVAILABLE);
		}
		else
			return new ResponseEntity<>(new Mensaje("No existe documento con el id: " + documento.getId()), HttpStatus.CONFLICT);
    }
	
	public ResponseEntity<?> createDocument(Descripcion descripcion) {	
		long idDocumento = catalogoDB.createDocumento(descripcion);
		
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
		Documento registro = catalogoDB.getDocumento(id);
		if (registro != null) {
			catalogoDB.deleteDocumento(id);
			return new ResponseEntity<>( HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(new Mensaje("El documento con id: " + id + ", no existe."), HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<?> getMunicipios(long idEstado) {	
		return new ResponseEntity<>(catalogoDB.getMunicipios(idEstado), HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getLocalidades(long idEstado, long idMunicipio) {	
		return new ResponseEntity<>(catalogoDB.getLocalidades(idEstado, idMunicipio), HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getColonias(long idEstado, long idMunicipio, long idLocalidad) {	
		return new ResponseEntity<>(catalogoDB.getColonias(idEstado, idMunicipio, idLocalidad), HttpStatus.OK);	
    }
	
	public ResponseEntity<?> getClinicas() {	
		return new ResponseEntity<>(catalogoDB.getClinicas(), HttpStatus.OK);	
    }
}
