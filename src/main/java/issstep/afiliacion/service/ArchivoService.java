package issstep.afiliacion.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import issstep.afiliacion.db.ArchivoDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.db.BeneficiarioDB;
import issstep.afiliacion.model.Archivo;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.utils.UtilsImage;

import javax.servlet.http.*;

@Service
public class ArchivoService{
	
	private static final String IMG_PNG = "png";
	private static final String IMG_JPG = "jpg";
	private static final String IMG_JPEG = "jpeg";
	private static final String FOTO = "foto";

	
	  
   DataFormatter formatter = new DataFormatter();
   SimpleDateFormat format = new SimpleDateFormat("yyyy");
   
   @Autowired
   ArchivoDB archivoDB;
   
   @Autowired
   UsuarioDB usuarioDB;
   
   @Autowired
   BeneficiarioDB beneficiarioDB;
  
	public ResponseEntity<?> uploadDocumento( long noControl, long noPreAfiliacion, long claveParentesco, long claveTipoArchivo,  MultipartFile uploadedFile, HttpServletResponse response) {
		try{
			
			
				Archivo archivoAnt = archivoDB.getArchivoByNoControlAndNoPreAfiliacion(noControl, noPreAfiliacion);
				
				if (archivoAnt != null) {
					System.out.println("Eliminacion de informacion");
					UtilsImage.deleteDocto(archivoAnt.getUrlArchivo());
					archivoDB.delete(archivoAnt.getClaveDocumento());
				}
		
			
			long idArchivoRegistrado;
			
			String desTipoDocto = archivoDB.getTipoArchivoByParentesco(claveParentesco, claveTipoArchivo);
			
			if(uploadedFile.getOriginalFilename().length()>60)  
				return new ResponseEntity<>(new Mensaje("Nombre de archivo demasiado largo"), HttpStatus.CONFLICT);
			
			else if(desTipoDocto == null)
				return new ResponseEntity<>(new Mensaje("No existe el tipo de documento"), HttpStatus.CONFLICT);
				
			else {
				Beneficiario beneficiario = beneficiarioDB.getBeneficiario(noControl, noPreAfiliacion, claveParentesco);
				
				if (beneficiario != null) {
					
					String resultado = UtilsImage.uploadFileToServer(UtilsImage.toPrettyURL(desTipoDocto), (MultipartFile) uploadedFile, "Persona"+noControl+noPreAfiliacion+claveParentesco+claveTipoArchivo+"-"+UtilsImage.toPrettyURL(desTipoDocto));
					
					
					Archivo archivo = new Archivo();
					
					archivo.setNoControl(noControl);
					archivo.setNoPreAfiliacion(noPreAfiliacion);
					archivo.setNoBeneficiario(beneficiario.getNoBeneficiario());  
					archivo.setClaveParentesco(claveParentesco);
					archivo.setClaveTipoArchivo(claveTipoArchivo);
					archivo.setNombre(uploadedFile.getOriginalFilename());
					archivo.setUrlArchivo(resultado);
					archivo.setEsValido(0);
					archivo.setClaveUsuarioRegistro(beneficiario.getClaveUsuarioRegistro());
					archivo.setFechaRegistro(new Timestamp(new Date().getTime()));					
					archivo.setClaveUsuarioModificacion(beneficiario.getClaveUsuarioModificacion());
					archivo.setEstatus(1);
					
					idArchivoRegistrado = archivoDB.insertarArchivo(archivo);
					
					/* archivoDB.insertarArchivo(idUsuario, idArchivoRegistrado); */
					if (idArchivoRegistrado != 0)
						return new ResponseEntity<>(archivo, HttpStatus.OK);
					else 
						return new ResponseEntity<>(new Mensaje("No fue posible registrar el archivo"), HttpStatus.CONFLICT);
				}
				else 
					return new ResponseEntity<>(new Mensaje("No existe un beneficiario"), HttpStatus.BAD_REQUEST); 
			}
			
	
			
		}catch(Exception ex){
			System.err.println("Exception ArchivoService.uploadDocto ");
			ex.printStackTrace();
			return new ResponseEntity<>(new Mensaje("Errot en el servidor"), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	public ResponseEntity<?> updateDocumento( long claveDocumento,  MultipartFile uploadedFile, HttpServletResponse response) {
		try{
			
			
				Archivo archivo = archivoDB.getArchivo(claveDocumento);
				
				if (archivo == null) 
					return new ResponseEntity<>(new Mensaje("El archivo no existe"), HttpStatus.NO_CONTENT);
		
				System.out.println("Eliminacion de informacion");
				UtilsImage.deleteDocto(archivo.getUrlArchivo());
				
				String desTipoDocto = archivoDB.getTipoArchivoByParentesco(archivo.getClaveParentesco(), archivo.getClaveTipoArchivo());
				
				String resultado = UtilsImage.uploadFileToServer(UtilsImage.toPrettyURL(desTipoDocto), 
																 (MultipartFile) uploadedFile, 
																 "Persona"+archivo.getNoControl()+archivo.getNoPreAfiliacion()+archivo.getClaveParentesco()+archivo.getClaveTipoArchivo()+"-"+UtilsImage.toPrettyURL(desTipoDocto));
				archivo.setNombre(uploadedFile.getOriginalFilename());
				archivo.setUrlArchivo(resultado);
				archivo.setEsValido(0);
				archivo.setEstatus(1);
				archivo.setFechaRegistro(new Timestamp(new Date().getTime()));	
				
				if (archivoDB.update(archivo) == -1)
					return new ResponseEntity<>(new Mensaje("No fue posible elimiar el registro del archivo"), HttpStatus.INTERNAL_SERVER_ERROR);
				
				return new ResponseEntity<>(new Mensaje("Archivo actualizado"), HttpStatus.OK);
		}
		catch(Exception ex){
			System.err.println("Exception ArchivoService.uploadDocto ");
			ex.printStackTrace();
			return new ResponseEntity<>(new Mensaje("Errot en el servidor"), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	public ResponseEntity<?> dowloadDocumento(long claveDocumento, HttpServletResponse response) {
		try {
			
			Archivo archivo = archivoDB.getArchivo(claveDocumento);
			System.out.println(archivo);
			
			if(archivo != null) {
			
				String nombreTemp = archivo.getUrlArchivo();
				InputStream imputStream = new FileInputStream(new File(nombreTemp));
				byte[] document = IOUtils.toByteArray(imputStream);
				String ext = FilenameUtils.getExtension(nombreTemp);
				String x = (!ext.equalsIgnoreCase("pdf"))?"image":"application";
				HttpHeaders header = new HttpHeaders();
				header.setContentType(new MediaType(x,ext));
				header.set("Content-Disposition", "inline; filename=" + nombreTemp);
				header.setContentLength(document.length);
				return new ResponseEntity<>(document, header, HttpStatus.OK);

			}
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		} catch (Exception ex) {
			System.err.println("Exception ArchivoService.downloadDocumento");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> listaDocumentos(long idParentesco, HttpServletResponse response) {
		try {
			
			//Usuario usuario = usuarioDB.getUsuarioById(idParentesco);
			
			// if(usuario != null) {
				return new ResponseEntity<>(archivoDB.getDocumentosByParentesco(idParentesco),  HttpStatus.OK);
			// }
			// return new ResponseEntity<>(new Mensaje("No existe el usuario"), HttpStatus.CONFLICT);
		
		} catch (Exception ex) {
			System.err.println("Exception ArchivoService.downloadDocumento");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> listaArchivos(long noControl, long noPreAfiliacion, long claveParentesco, HttpServletResponse response) {
		Beneficiario beneficiario = beneficiarioDB.getBeneficiario(noControl, noPreAfiliacion, claveParentesco);
		
		if (beneficiario != null) {
			try {
				
				//Usuario usuario = usuarioDB.getUsuarioById(idParentesco);
				
				// if(usuario != null) {
				return new ResponseEntity<>(archivoDB.getArchivos(noControl, noPreAfiliacion, beneficiario.getNoBeneficiario(), claveParentesco),  HttpStatus.OK);
				// }
				// return new ResponseEntity<>(new Mensaje("No existe el usuario"), HttpStatus.CONFLICT);
				
			} catch (Exception ex) {
				System.err.println("Exception ArchivoService.downloadDocumento");
				ex.printStackTrace();
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}		
		}
		else 
			return new ResponseEntity<>(new Mensaje("No existe un beneficiario"), HttpStatus.BAD_REQUEST); 
			
	}
	
	public ResponseEntity<?> deleteDocumento(long claveDocumento, HttpServletResponse response) {
		try {
			
			Archivo archivo = archivoDB.getArchivo(claveDocumento);
			if(archivo == null)
				return new ResponseEntity<>(new Mensaje("Documento no encontrado"), HttpStatus.NOT_FOUND);
			
			UtilsImage.deleteDocto(archivo.getUrlArchivo());
			if (archivoDB.delete(claveDocumento) == -1)
				return new ResponseEntity<>(new Mensaje("El documento no se pudo eliminar"), HttpStatus.INTERNAL_SERVER_ERROR);
		
			return new ResponseEntity<>(new Mensaje("Eliminacion correcta"), HttpStatus.OK);	
		} 
		catch (Exception ex) {
			System.err.println("Exception ArchivoService.descargaDocumento");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
		
}
