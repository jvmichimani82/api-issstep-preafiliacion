package issstep.afiliacion.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks{
	
	@Autowired
	@Qualifier("afiliacionJdbcTemplate")
	JdbcTemplate afiliacionDBTemplate;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(cron = "0 0 * * * ?")
	public void scheduledTask() {
		
		 	try {
				int resultado =	afiliacionDBTemplate.queryForObject("SELECT 1", Integer.class);
				System.out.println("time: "+ dateFormat.format(new Date()) +" ->"+ resultado);
			}
			catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
				readBashScript();
			}
			catch (Exception e) {
				e.printStackTrace();
				readBashScript();
			}
		    
		    
	}
	
	public static void readBashScript() {
        try {
            Process proc = Runtime.getRuntime().exec("/usr/preafiliacion/script.sh"); //Whatever you want to execute
            BufferedReader read = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            while (read.ready()) {
                System.out.println(read.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
 
}
