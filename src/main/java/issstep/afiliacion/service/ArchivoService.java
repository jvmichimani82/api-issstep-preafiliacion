package issstep.afiliacion.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Row;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import issstep.afiliacion.db.ArchivoDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.model.Archivo;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.Usuario;
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

	public ResponseEntity<?> uploadDocumento(long idTrabajador, long idBeneficiario, long idParentesco, long noTipoArchivo,  MultipartFile uploadedFile, HttpServletResponse response) {
		try{
			long idArchivoRegistrado;
			String desTipoDocto = archivoDB.getTipoArchivoByParentesco(idParentesco, noTipoArchivo);
			
			if(uploadedFile.getOriginalFilename().length()>60)  
				return new ResponseEntity<>(new Mensaje("Nombre de archivo demasiado largo"), HttpStatus.CONFLICT);
			
			else if(desTipoDocto == null)
				return new ResponseEntity<>(new Mensaje("No existe el tipo de documento"), HttpStatus.CONFLICT);
				
			else {
					
				String resultado = UtilsImage.uploadFileToServer(UtilsImage.toPrettyURL(desTipoDocto), (MultipartFile) uploadedFile, "Persona"+idTrabajador+idBeneficiario+idParentesco+noTipoArchivo+"-"+UtilsImage.toPrettyURL(desTipoDocto));
				
				Archivo archivo = new Archivo();
						
						archivo.setNoTrabajador(idTrabajador);
						archivo.setNoBeneficiario(idParentesco);
						archivo.setNoParentesco(idTrabajador);
						archivo.setNoTArchivo(noTipoArchivo);
						archivo.setNombre(uploadedFile.getOriginalFilename());
						archivo.setUrlArchivo(resultado);
						archivo.setValidado(0);
						archivo.setFechaRegistro(new Timestamp(new Date().getTime()));
						archivo.setActivo(1);

				idArchivoRegistrado = archivoDB.insertarArchivo(archivo);
				
				/* archivoDB.insertarArchivo(idUsuario, idArchivoRegistrado); */
				
				return new ResponseEntity<>(archivo, HttpStatus.OK);
			
			}
			
	
			
		}catch(Exception ex){
			System.err.println("Exception ArchivoService.uploadDocto ");
			ex.printStackTrace();
			return new ResponseEntity<>(new Mensaje("Errot en el servidor"), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	public ResponseEntity<?> dowloadDocumento(long idTrabajador, long idBeneficiario, long idParentesco, long noTipoArchivo, HttpServletResponse response) {
		try {
			
			Archivo archivo = archivoDB.getArchivo(idTrabajador, idBeneficiario, idParentesco, noTipoArchivo);
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
	
	public ResponseEntity<?> listaArchivos(long idTrabajador, long idBeneficiario, long idParentesco, HttpServletResponse response) {
		try {
			
			//Usuario usuario = usuarioDB.getUsuarioById(idParentesco);
			
			// if(usuario != null) {
				return new ResponseEntity<>(archivoDB.getArchivos(idTrabajador, idBeneficiario, idParentesco),  HttpStatus.OK);
			// }
			// return new ResponseEntity<>(new Mensaje("No existe el usuario"), HttpStatus.CONFLICT);
		
		} catch (Exception ex) {
			System.err.println("Exception ArchivoService.downloadDocumento");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> deleteDocumento(long idTrabajador, long idBeneficiario, long idParentesco, long noTipoArchivo, HttpServletResponse response) {
		try {
			
			/* Archivo archivo = archivoRepository.findOne(idArchivo);
			if(archivo != null) {
				UtilsImage.deleteDocto(archivo.getUrlArchivo());
				archivoRepository.delete(archivo);
			
			}*/
			return new ResponseEntity<>(new Mensaje("Eliminacion correcta"), HttpStatus.NO_CONTENT);
		
		} catch (Exception ex) {
			System.err.println("Exception ArchivoService.descargaDocumento");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
		
}
