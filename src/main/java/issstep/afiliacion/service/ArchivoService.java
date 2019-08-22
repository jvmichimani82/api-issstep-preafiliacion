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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import issstep.afiliacion.db.ArchivoDB;
import issstep.afiliacion.db.UsuarioDB;
import issstep.afiliacion.db.BeneficiarioDB;
import issstep.afiliacion.db.DerechohabienteDB;
import issstep.afiliacion.model.Archivo;
import issstep.afiliacion.model.Mensaje;
import issstep.afiliacion.model.ResultadoValidacion;
import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.InfoPersona;
import issstep.afiliacion.utils.UtilsImage;

import javax.servlet.http.*;

@Service
public class ArchivoService{
	
	/* private static final String IMG_PNG = "png";
	private static final String IMG_JPG = "jpg";
	private static final String IMG_JPEG = "jpeg";
	private static final String FOTO = "foto"; */

	
	  
   DataFormatter formatter = new DataFormatter();
   SimpleDateFormat format = new SimpleDateFormat("yyyy");
   
   @Autowired
   ArchivoDB archivoDB;
   
   @Autowired
   UsuarioDB usuarioDB;
   
   @Autowired
   BeneficiarioDB beneficiarioDB;
   
   @Autowired
   DerechohabienteDB derechohabienteDB;
   
  
	public ResponseEntity<?> uploadDocumento( long noControlTitular, long noControl, long noPreAfiliacion, long claveParentesco, long claveTipoArchivo,  MultipartFile uploadedFile, HttpServletResponse response) {
		ResultadoValidacion resultadoValidacion = validaEstatusRegistro (noControlTitular, noControl, noPreAfiliacion, claveParentesco);
		
		if (!resultadoValidacion.isEsValido())
			return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.CONFLICT);
		
		try{
			Archivo archivo = archivoDB.getArchivoByPersonaAndParentesco(noControl, noPreAfiliacion, claveParentesco,  claveTipoArchivo);
			
			if (archivo != null)
				return new ResponseEntity<>(new Mensaje("Ya existe un archivo"), HttpStatus.CONFLICT);
			
			long idArchivoRegistrado;
			
			String desTipoDocto = archivoDB.getTipoArchivoByParentesco(claveParentesco, claveTipoArchivo);
			
			if(uploadedFile.getOriginalFilename().length()>60)  
				return new ResponseEntity<>(new Mensaje("Nombre de archivo demasiado largo"), HttpStatus.CONFLICT);
			
			else if(desTipoDocto == null)
				return new ResponseEntity<>(new Mensaje("No existe el tipo de documento"), HttpStatus.CONFLICT);
				
			else {
				Beneficiario beneficiario = beneficiarioDB.getBeneficiario(noControlTitular, noControl, noPreAfiliacion, claveParentesco);
				
				if (beneficiario != null) {
					
					String resultado = UtilsImage.uploadFileToServer(UtilsImage.toPrettyURL(desTipoDocto), (MultipartFile) uploadedFile, "Persona"+noControl+noPreAfiliacion+claveParentesco+claveTipoArchivo+"-"+UtilsImage.toPrettyURL(desTipoDocto));
					
					archivo = new Archivo();
					
					archivo.setNoControl(noControl);
					archivo.setNoPreAfiliacion(noPreAfiliacion);
					archivo.setNoBeneficiario(beneficiario.getNoBeneficiario());  
					archivo.setClaveParentesco(claveParentesco);
					archivo.setClaveTipoArchivo(claveTipoArchivo);
					archivo.setNombre(uploadedFile.getOriginalFilename());
					archivo.setUrlArchivo(resultado);
					archivo.setEsValido(2);
					archivo.setClaveUsuarioRegistro(beneficiario.getClaveUsuarioRegistro());
					archivo.setFechaRegistro(new Timestamp(new Date().getTime()));					
					archivo.setClaveUsuarioModificacion(beneficiario.getClaveUsuarioModificacion());
					archivo.setEstatus(1);
					
					idArchivoRegistrado = archivoDB.insertarArchivo(archivo);
					
					/* archivoDB.insertarArchivo(idUsuario, idArchivoRegistrado); */
					if (idArchivoRegistrado != 0) {
						archivo.setClaveDocumento(idArchivoRegistrado);
						return new ResponseEntity<>(archivo, HttpStatus.OK);
					}
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
			
			ResultadoValidacion resultadoValidacion = validaEstatusRegistro (archivo.getNoControlTitular(), 
																			 archivo.getNoControl(), 
																			 archivo.getNoPreAfiliacion(),
																			 archivo.getClaveParentesco());
			
			if (!resultadoValidacion.isEsValido())
				return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.CONFLICT);
			
			UtilsImage.deleteDocto(archivo.getUrlArchivo());
			
			String desTipoDocto = archivoDB.getTipoArchivoByParentesco(archivo.getClaveParentesco(), archivo.getClaveTipoArchivo());
			
			String resultado = UtilsImage.uploadFileToServer(UtilsImage.toPrettyURL(desTipoDocto), 
															 (MultipartFile) uploadedFile, 
															 "Persona"+archivo.getNoControl()+archivo.getNoPreAfiliacion()+archivo.getClaveParentesco()+archivo.getClaveTipoArchivo()+"-"+UtilsImage.toPrettyURL(desTipoDocto));
			archivo.setNombre(uploadedFile.getOriginalFilename());
			archivo.setUrlArchivo(resultado);
			archivo.setEsValido(2);
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
			// System.out.println(archivo);
			
			if(archivo != null) {
				ResultadoValidacion resultadoValidacion = validaEstatusRegistro (archivo.getNoControlTitular(),  
																				 archivo.getNoControl(), 
																				 archivo.getNoPreAfiliacion(),
																				 archivo.getClaveParentesco());
				
				if (!resultadoValidacion.isEsValido())
					return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.CONFLICT);
				
			
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
	
	public ResponseEntity<?> listaArchivos(long noControlTitular, long noControl, long noPreAfiliacion, long claveParentesco, HttpServletResponse response) {
		Beneficiario beneficiario = beneficiarioDB.getBeneficiario(noControlTitular, noControl, noPreAfiliacion, claveParentesco);
		
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
			
			ResultadoValidacion resultadoValidacion = validaEstatusRegistro (archivo.getNoControlTitular(),  
																			 archivo.getNoControl(), 
																			 archivo.getNoPreAfiliacion(),
																			 archivo.getClaveParentesco());
			
			if (!resultadoValidacion.isEsValido())
				return new ResponseEntity<>(new Mensaje(resultadoValidacion.getMensaje()), HttpStatus.CONFLICT);
			
			Usuario usuarioLogin = getInfoLogin();
			if (usuarioLogin == null)
				return new ResponseEntity<>(new Mensaje("Usuario no autentificado"), HttpStatus.BAD_REQUEST);
			
			if (archivo.getEsValido() == 1 && !usuarioLogin.getRol().equals("ADMINISTRADOR")) 
				return new ResponseEntity<>(new Mensaje("El estatus del documento es valido"), HttpStatus.NOT_FOUND);
			
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
	
	public ResponseEntity<?> updateValidacionDocto( long claveDocumento, int estatusValidacion, HttpServletResponse response) {
		if (estatusValidacion < 0 || estatusValidacion > 2)
			return new ResponseEntity<>(new Mensaje("Estatus invalido"), HttpStatus.BAD_REQUEST);
		
		try {
			
			Archivo archivo = archivoDB.getArchivo(claveDocumento);
			
			if(archivo == null)
				return new ResponseEntity<>(new Mensaje("Documento no encontrado"), HttpStatus.NOT_FOUND);
			
			archivo.setEsValido(estatusValidacion);
			
			if (archivoDB.update(archivo) == -1)
				return new ResponseEntity<>(new Mensaje("El no pudo actualizar el documento"), HttpStatus.INTERNAL_SERVER_ERROR);
		
			return new ResponseEntity<>(new Mensaje("Actualizacion correcta"), HttpStatus.OK);	
		} 
		catch (Exception ex) {
			System.err.println("Exception ArchivoService.updateValidacionDocto");
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	ResultadoValidacion validaEstatusRegistro(long noControlTitular, long noControl, long noPreAfiliacion, long claveParentesco) {
		ResultadoValidacion resultadoValidacion = new ResultadoValidacion();
		
		InfoPersona infoPersona = creaInforPersona(noControlTitular, noControl, noPreAfiliacion, claveParentesco);
				
		Derechohabiente derechohabiente =  derechohabienteDB.getPersonaByNoControlNoPreafiliacion( infoPersona );
		
		if (derechohabiente == null) {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("No existe el derechohabiente con número de control: " + noControl
										+  "y número de afiliación: " + noPreAfiliacion);
			return resultadoValidacion;
		}
		
		int estatus = derechohabiente.getEstatus();
		
		if (estatus == 9)  {
			resultadoValidacion.setEsValido(false);
			resultadoValidacion.setMensaje("El derechohabiente no se puede actualizar");
			return resultadoValidacion;
		}
		
		resultadoValidacion.setEsValido(true);
		resultadoValidacion.setMensaje("");
		return resultadoValidacion;
		
	}
	
	Usuario getInfoLogin() {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getName();
		if (user == "anonymousUser") 
			return null;			
		Usuario usuario =  usuarioDB.getUsuarioByColumnaStringValor("LOGIN", user);
	
		return usuario;
	}
	
	InfoPersona creaInforPersona(long noControlTitular, long noControl, long noPreAfiliacion, long claveParentesco) {
		InfoPersona infoPersona = new InfoPersona();
		infoPersona.setNoControlTitular(noControlTitular);
		infoPersona.setNoControl(noControl);
		infoPersona.setNoPreAfiliacion(noPreAfiliacion); 
		infoPersona.setClaveParentesco(claveParentesco);
		
		return infoPersona;
	}
		
}
