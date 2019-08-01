package issstep.afiliacion.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
   private static final String expRegParaNumero = "^[0-9]+";
   private static final String expRegParaCURP = "^([A-Za-z]{1}[AEIOUaeiou]{1}[A-Za-z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HMhm]{1}([A|a][S|s]|[B|b][C|c]|[B|b][S|s]|[C|c][C|c]|[C|c][S|s]|[C|c][H|h]|[C|c][L|l]|[C|c][M|m]|[D|d][F|f]|[D|d][G|g]|[G|g][T|t]|[G|g][R|r]|[H|h][G|g]|[J|j][C|c]|[M|m][C|c]|[M|m][N|n]|[M|m][S|s]|[N|n][T|t]|[N|n][L|l]|[O|o][C|c]|[P|p][L|l]|[Q|q][T|t]|[Q|q][R|r]|[S|s][P|p]|[S|s][L|l]|[S|s][R|r]|[T|t][C|c]|[T|t][S|s]|[T|t][L|l]|[V|v][Z|z]|[Y|y][N|n]|[Z|z][S|s]|[N|n][E|e])[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]{3}[0-9A-Za-z]{2})$";
   private static final String expRegPatronCURP = "^([A-Z]{4}[0-9]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{1,6})|([A-Z]{4}[0-9]{6}[A-Z]{6}[A-Z0-9]{1,2})$";
   private static final String expRegPatronEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";
   	
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
   
   public static boolean verificaValorVSExpReg(String valor, String ExpReg) {
	   Pattern patron = Pattern.compile(ExpReg);
	   Matcher result = patron.matcher(valor);
	   
	   return result.find();
   }
   
   public static boolean esNumero(String valor) {	   
	   return verificaValorVSExpReg( valor,  expRegParaNumero);
   }
   
   public static boolean esCURP(String valor) {	   
	   return verificaValorVSExpReg( valor,  expRegParaCURP);
   }
   
   public static boolean esPatronCURP(String valor) {	   
	   return verificaValorVSExpReg( valor,  expRegPatronCURP);
   }
   
   public static boolean esEmail(String valor) {	   
	   return verificaValorVSExpReg( valor,  expRegPatronEmail);
   }
   

}
