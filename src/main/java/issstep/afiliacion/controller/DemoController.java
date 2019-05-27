package issstep.afiliacion.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	

	@RequestMapping(value = "/getMYUser")
	public String getMYUser() {
		Map<String, Object> map = new HashMap<String, Object>();
		String query = " select * from usuario limit 1";
		try {
			map = mysqlTemplate.queryForMap(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "MySQL Data: " + map.toString();
	}
}