package issstep.afiliacion.utils;

import java.util.Properties;

import javax.imageio.ImageIO;

import org.springframework.context.MessageSource;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	
   public static String loadPropertie(String propiedad){
	   try{
		    Properties properties = new Properties();
			properties.load(Utils.class.getClassLoader().getResourceAsStream("persistence.properties"));
			
			if(properties.get(propiedad) == null)	
				properties.load(Utils.class.getClassLoader().getResourceAsStream("application.properties"));
			
			return (String) properties.get(propiedad);

	   }
	   catch(Exception e){
		   
			   e.printStackTrace();	
	   			System.err.println("Exception Utils.loadPropertie");
			   return null;
		  
	   }
		
	}
	 
   public static String sha256(String base) {
	    try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}
   
   public static Timestamp getFechaFromString(String fecha){
	   SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
      
       try {

           Date date = formatter.parse(fecha);
          return new Timestamp(date.getTime());

       } catch (ParseException e) {
    	   
           e.printStackTrace();
           return null;
       }
   }
   
   public static float getFloatFromString(String monto) {
	   try {
		   String sin = monto.replace(",", "");
		   return Float.parseFloat(sin);

       } catch (Exception e) {
    	   
           e.printStackTrace();
           return 0l;
       }
   }
   

}
