package issstep.afiliacion.utils;



import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;


public class UtilsImage {

	private static final String IMG_PNG = "png";
	private static final String IMG_JPG = "jpg";
	private static final String IMG_JPEG = "jpeg";
	
		public static String getTipoDeSO(){
		   String SO = System.getProperty("os.name");
		   System.out.println(SO);
		   System.out.println(getURLBySO());
		   return SO;
		}
		
		public static boolean writeFile(String carpeta, String nombre, byte[] content, boolean imgs) {
			try {
				FileOutputStream output = new FileOutputStream((new File(getFullRuta(carpeta,imgs) + nombre)));
				try{
					IOUtils.write(content, output);
				}catch (Exception ex) {
					ex.printStackTrace();
		        	System.err.println("Exception writeFile " + ex.toString());
		        }finally {
					output.close();
				}
				return true;
			} catch (Exception exc) {
				exc.printStackTrace();
				return false;
			}
		}
		
		public static String getFullRuta(String origen, boolean imgs) {
			if(getTipoDeSO().startsWith("Windows"))
				return getURLBySO() + origen + "\\" + (imgs ? "imagenes\\" : "doctos\\");
			else
				return "/repo_app/" + origen + "/" + (imgs ? "imagenes/" : "doctos/");
		}
		
		public static String getURLBySO() {

		   	String SO = System.getProperty("os.name");
		   	String urlDefault = null;
		   	
		   	if(SO.startsWith("Windows"))
				urlDefault = loadPropertie("url_windows");
			else if(SO.startsWith("Mac OS X"))
				urlDefault = loadPropertie("url_unix");
			else
				urlDefault = loadPropertie("url_unix");
		 	return urlDefault;
		}
		
		public static String loadPropertie(String propiedad){
			   try{
				    Properties properties = new Properties();
					properties.load(UtilsImage.class.getClassLoader().getResourceAsStream("application.properties"));
					
					return (String) properties.get(propiedad);

			   }
			   catch(Exception e){
				   
					   e.printStackTrace();	
			   			System.err.println("Exception Utils.loadPropertie");
					   return null;
				  
			   }
				
			}
		
		public static String uploadFileToServer(String tipoDocto, MultipartFile archivo, String identificador)  throws Exception{
           	try {
           		String nombreTemp = archivo.getOriginalFilename().toLowerCase();
	       		int index = nombreTemp.lastIndexOf(".");
  	       		String extension = nombreTemp.substring(index+1, nombreTemp.length()).toLowerCase();
  	       		String nombreArchivo = null;
  	       		String rutaCompleta;
  	       		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
  	       		
  	       		if(extension.equals(IMG_PNG) || extension.equals(IMG_JPG)|| extension.equals(IMG_JPEG)){
  	       			byte[] resizedContent = resizeImage(archivo.getBytes(), RenderingHints.KEY_ANTIALIASING, false, extension);
  	       			nombreArchivo = identificador+"-" +sdf.format(new Date())+ "." + extension;
  	           		rutaCompleta = getFullRuta(tipoDocto, true);
  	           		creaCarpetas(rutaCompleta);
  	           		writeFile(tipoDocto, nombreArchivo, resizedContent, true);
  	       		}
  	       		else {
  	       			nombreArchivo = identificador+"-" +sdf.format(new Date())+ "." + extension;
	           		rutaCompleta = getFullRuta(tipoDocto, false);
	           		creaCarpetas(rutaCompleta);
	           		writeFile(tipoDocto, nombreArchivo, archivo.getBytes(), false);
  	       		}
  	       		return rutaCompleta + nombreArchivo;
  	   	} 
  			catch (Exception ex) {
  				ex.printStackTrace();
  	        	System.err.println("Error en cargaArchivosToServer " + ex.toString());
  	        	return null;
   			}
  	}
		
		public static void creaCarpetas(String ruta) {
			try {
			   	java.io.File directorio = new File(ruta);
			   	directorio.mkdirs();
			 }catch (Exception ioex) {
			   	ioex.printStackTrace();
			   	System.err.println("Exception creaCarpetas " + ioex.toString());
			 }
		}

		
		 public static byte[] resizeImage(byte[] imagen, Object hint, boolean higherQuality, String extencion) throws IOException {
				InputStream in = null;
				BufferedImage ret = null;
				try {
					
					in = new ByteArrayInputStream(imagen);
					BufferedImage img = ImageIO.read(in);
					
					int type = (img.getTransparency() == Transparency.OPAQUE) ?
					BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
					ret = (BufferedImage)img;
					int w, h;
					if (higherQuality) {
						w = (int) (img.getWidth() * 1.5);
						h = (int) (img.getHeight() * 1.5);
					} 
					else {
						w = img.getWidth();
						h = img.getHeight();
					}
				
					BufferedImage tmp = new BufferedImage(w, h, type);
					Graphics2D g2 = tmp.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2.drawImage(ret, 0, 0, w, h, null);
					g2.dispose();
					
					ret = tmp;
				}catch (Exception ex) {
					ex.printStackTrace();
					System.err.println("Exception getBytes " + ex.toString());
		       }finally {
					in.close();
		       }
			
			return getBytes(ret, extencion);
			}
		 
		 public static byte[] getBytes(BufferedImage img, String extencion) throws IOException {
				ByteArrayOutputStream byar = new ByteArrayOutputStream();
				try {
					ImageIO.write(img, extencion, byar);
				}catch (Exception ex) {
					ex.printStackTrace();
		       	System.err.println("Exception getBytes " + ex.toString());
		       }finally {
					byar.close();
		       }
				return byar.toByteArray();
			}
		 
		 public static void deleteDocto(String ruta) {
				File fichero = new File(ruta);
				if (fichero.delete())
					System.out.println("El fichero ha sido borrado satisfactoriamente");
				else
					System.out.println("El fichero no puede ser borrado");
			}
			
			
		public static String toPrettyURL(String cad) {
	        return Normalizer.normalize(cad.toLowerCase(), Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	            .replaceAll("[^\\p{Alnum}]+", "-");
	    }
}
