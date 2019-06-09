package issstep.afiliacion.service;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import issstep.afiliacion.utils.Utils;



@Service
public class MailService {
 
	@Autowired 
    private JavaMailSender mailSender; 
	
	@Autowired 
    private TemplateEngine templateEngine;
    
	private final static String PRODUCCION = "3";
	private final static String PRUEBAS = "2";
	String urlSistema = Utils.loadPropertie("url_sistema");
	String header_correo = Utils.loadPropertie("header_correo");
	String nombre_plataforma = Utils.loadPropertie("nombre_plataforma");
	//String mail_copia = Utils.loadPropertie("mail.copia");

    public void prepareAndSendBienvenida( final String recipient,  final String nombre,	final String usuario,final String token, final Long num_identificacion) {
    	 try {     
    		 String urlConfirmacionCorreo = Utils.loadPropertie("url_confirmacionCorreo");
    		 MimeMessage message = mailSender.createMimeMessage();
		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
		     helper.setFrom("AFILIACION");
		     helper.setTo(recipient);
		     helper.setSubject("Correo de Bienvenida");
		     
		     /*if(Utils.loadPropertie("ambiente").equals(PRODUCCION)){
		    	 helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }else if (Utils.loadPropertie("ambiente").equals(PRUEBAS)){
		    	 helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }		*/

		     final Context context = new Context(); 
		     context.setVariable("nombre_plataforma", nombre_plataforma);
		     context.setVariable("nombre", nombre);
		     context.setVariable("usuario", usuario);
		     context.setVariable("num_identificacion", num_identificacion);
		     context.setVariable("urlConfirmacionCorreo", urlConfirmacionCorreo+"/"+token);
		     context.setVariable("header_correo", header_correo);
		     context.setVariable("logo1", Utils.loadPropertie("url.logo1"));
		     context.setVariable("logo2", Utils.loadPropertie("url.logo2"));
		     
		      
		     final String htmlContent = templateEngine.process("plantilla_activa_cuenta", context); 
		     helper.setText(htmlContent, true); 
		        
		     mailSender.send(message); 

    	    } catch (Exception e) {
    	    	System.err.println("Exception prepareAndSendBienvenida");
    			e.printStackTrace();
    	    }
    }
    

    
    public void prepareAndSendResetPass( final String recipient,  final String nombre ,final String token) {
   	 try {     
   		 String urlReset = Utils.loadPropertie("url_cambiarPassword")+"/"+token;
   		 MimeMessage message = mailSender.createMimeMessage();
		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
		     helper.setFrom("FABRICA_DE_TALENTOS");
		     helper.setTo(recipient);
		     helper.setSubject("Solicitud para reestablecer contraseña");
		     
		     if(Utils.loadPropertie("ambiente").equals(PRODUCCION)){
		    	helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }else if (Utils.loadPropertie("ambiente").equals(PRUEBAS)){
		    	 helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }	

		     final Context context = new Context(); 
		     context.setVariable("nombre_plataforma", nombre_plataforma);
		     context.setVariable("nombre", nombre);;
		     context.setVariable("urlReset", urlReset);
		     context.setVariable("header_correo", header_correo);
		     context.setVariable("logo1", Utils.loadPropertie("url.logo1"));
		     context.setVariable("logo2", Utils.loadPropertie("url.logo2"));
		      
//		     context.setVariable("logo1", logo1);
//		     context.setVariable("logo2", logo2);
//		     context.setVariable("logo3", logo3);
		     
		     final String htmlContent = templateEngine.process("mailing_resetPass", context); 
		     helper.setText(htmlContent, true); 
		        
		     mailSender.send(message); 

   	    } catch (Exception e) {
   	    	System.err.println("Exception prepareAndSendBienvenida");
   			e.printStackTrace();
   	    }
   }

 
}