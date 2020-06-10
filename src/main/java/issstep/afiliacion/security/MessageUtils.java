package issstep.afiliacion.security;

import java.util.Properties;
import org.springframework.stereotype.Component;

import issstep.afiliacion.utils.Utils;


@Component
public class MessageUtils {
 
    public static String getMessage(String key) {

    	  try{
  		    Properties properties = new Properties();
  			properties.load(Utils.class.getClassLoader().getResourceAsStream("application.properties"));
  			return (String) properties.get(key);
  	   }catch(Exception e){
  		    e.printStackTrace();	
     			System.err.println("Exception Utils.loadPropertie");
  		   return null;
  	   }
 
    }
}