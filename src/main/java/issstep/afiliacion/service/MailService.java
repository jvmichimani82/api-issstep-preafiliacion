package issstep.afiliacion.service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
	
	public static int noOfQuickServiceThreads = 20;
	
	/**
	 * this statement create a thread pool of twenty threads
	 * here we are assigning send mail task using ScheduledExecutorService.submit();
	 */
	private ScheduledExecutorService quickService = Executors.newScheduledThreadPool(noOfQuickServiceThreads); // Creates a thread pool that reuses fixed number of threads(as specified by noOfThreads in this case).
  

    public void prepareAndSendBienvenida( final String recipient,  final String nombre,	final String usuario,final String token, final Long num_identificacion) {
    	 try {     
    		 String urlConfirmacionCorreo = Utils.loadPropertie("url_confirmacionCorreo");
    		 MimeMessage message = mailSender.createMimeMessage();
		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
		     helper.setFrom("AFILIACION");
		     helper.setTo(recipient);
		     helper.setSubject("Correo de Bienvenida");
		     
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
		        
		     quickService.submit(new Runnable() {
					@Override
					public void run() {
						try{
							mailSender.send(message); 
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});


		     

    	    } catch (Exception e) {
    	    	System.err.println("Exception prepareAndSendBienvenida");
    			e.printStackTrace();
    	    }
    }
    
    public void SendMensaje1( final String recipient,  final String nombre) {
   	 try {     
   		 	MimeMessage message = mailSender.createMimeMessage();
		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
		     helper.setFrom("AFILIACION");
		     helper.setTo(recipient);
		     helper.setSubject("Notificacion ISSSTEP");
		     
		     final Context context = new Context(); 
		     context.setVariable("nombre_plataforma", nombre_plataforma);
		     context.setVariable("nombre", nombre);
		     context.setVariable("header_correo", header_correo);
		     
		      
		     final String htmlContent = templateEngine.process("plantilla_mensaje1", context); 
		     helper.setText(htmlContent, true); 
		        
		     quickService.submit(new Runnable() {
					@Override
					public void run() {
						try{
							mailSender.send(message); 
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});


		     

   	    } catch (Exception e) {
   	    	System.err.println("Exception SendMensaje");
   			e.printStackTrace();
   	    }
   }
    
    public void SendMensaje2( final String recipient,  final String nombre) {
      	 try {     
      		 	MimeMessage message = mailSender.createMimeMessage();
   		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
   		     helper.setFrom("AFILIACION");
   		     helper.setTo(recipient);
   		     helper.setSubject("Notificacion ISSSTEP");
   		     
   		     final Context context = new Context(); 
   		     context.setVariable("nombre_plataforma", nombre_plataforma);
   		     context.setVariable("nombre", nombre);
   		     context.setVariable("header_correo", header_correo);
   		     
   		      
   		     final String htmlContent = templateEngine.process("plantilla_mensaje2", context); 
   		     helper.setText(htmlContent, true); 
   		        
   		     quickService.submit(new Runnable() {
   					@Override
   					public void run() {
   						try{
   							mailSender.send(message); 
   						}catch(Exception e){
   							e.printStackTrace();
   						}
   					}
   				});


   		     

      	    } catch (Exception e) {
      	    	System.err.println("Exception SendMensaje");
      			e.printStackTrace();
      	    }
      }
   

    
    public void prepareAndSendResetPass( final String recipient,  final String nombre ,final String token) {
   	 try {     
   		 String urlReset = Utils.loadPropertie("url_cambiarPassword")+"/"+token;
   		 MimeMessage message = mailSender.createMimeMessage();
		     MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());	
		     helper.setFrom("AFILIACION");
		     helper.setTo(recipient);
		     helper.setSubject("Solicitud para reestablecer contraseña");
		     
		     /* if(Utils.loadPropertie("ambiente").equals(PRODUCCION)){
		    	helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }else if (Utils.loadPropertie("ambiente").equals(PRUEBAS)){
		    	 helper.setBcc("fabrica.talentos.2018@gmail.com");
		     }	*/

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