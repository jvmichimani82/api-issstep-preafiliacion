package issstep.afiliacion.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import javax.servlet.http.*;

@Service
public class SambaService{
	
    public boolean creaDirectorio(String ubicacion, String usuario, String passwd) {    	
    	boolean res = false;		
    	try{
    		
    		try {
    			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",usuario, passwd);
        		
        		ubicacion = "smb://"+ubicacion;
        		
        		SmbFile sFile = new SmbFile(ubicacion, auth);
         		
        		if(!sFile.exists()){        			
        			sFile = new SmbFile(ubicacion, auth);
        			sFile.mkdirs();        			
        		} else{
        	
        			sFile = new SmbFile(ubicacion, auth);
        			
        			if(!sFile.exists()) {        				
        				// Como no existe se crea la carpeta de la solicitud         			
             			sFile = new SmbFile(ubicacion, auth);
            			sFile.mkdir();            			
        			}         			
        		}
        		
        		res=true;
    		} catch (  final SmbAuthException sae) {
    			System.out.println("SMB *****ERROR: " + sae.getMessage());
    		} catch (MalformedURLException e) {
    			e.getCause(); 
    			System.out.println("SMB *****ERROR: " + e.getMessage());
             }
    		
        } catch (Exception e) {
         	System.out.println("*****Error: Archivos.creaDirectorio - " + e.getMessage()); 
         	e.printStackTrace();
        }
    	
        return res;
    }
    
	public String upload(MultipartFile file, String ubicacion, String nombreArchivo, String usuario, String passwd ) {
		String respuesta = null;
		String fileName = null;
		if (null != file ) {
           
			try {
            		String nombre = file.getOriginalFilename(); 
            		
            		// Si el campo contiene archivo 
            		if(nombre.length()>0) {
            			// Se obtiene la extension para realizar el nombramiento del archivo 
        				String extension = FilenameUtils.getExtension(nombre);
        				
            			// Se arma el nombre del archivo
                		fileName = nombreArchivo+"."+extension;
                		
                		byte[] bytes = file.getBytes();                              
                      
                		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", usuario, passwd);
                	
                		SmbFile sFile = new SmbFile("smb://"+ubicacion+ "//" + fileName, auth);
                      
                		SmbFileOutputStream sfos = new SmbFileOutputStream(sFile);
                        sfos.write(bytes);
                        sfos.flush();
                        sfos.close();                        
                       
            		}             		
                    respuesta = "smb://"+ubicacion+ "//" + fileName;                    
                  
    				 
                } catch (Exception e) {
                	
                	respuesta = null; 
                	System.out.println(" *** Error: " + e.getMessage());
                 }
              
        }
		
		return respuesta; 
	}
	
	
	
	public byte[] download(String nombreArchivo, String usuario, String passwd){
		byte[] byteArray = null; 
		//ByteArrayResource byteArrayR = null; 
	    try {
           	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", usuario, passwd);
        	SmbFile sFile = new SmbFile(nombreArchivo, auth);
	        SmbFileInputStream smbStream = new SmbFileInputStream(sFile);
	        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
	        int nRead;
	        byte[] data = new byte[1024];
	        while ((nRead = smbStream.read(data, 0, data.length)) != -1) {
	        	byteOutStream.write(data, 0, nRead);
	        }
	     
	        byteOutStream.flush();
	        smbStream.close();
	        byteArray = byteOutStream.toByteArray();
	        //byteArrayR = new ByteArrayResource(byteArray); 
        } catch (MalformedURLException e) {        	
        } catch (SmbException e) {
		} catch (UnknownHostException e) {
			} catch (IOException e) {
		}
        
        return byteArray;
    }


}
