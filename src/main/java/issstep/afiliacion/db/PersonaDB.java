package issstep.afiliacion.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Persona;
import issstep.afiliacion.model.Usuario;

@Component
public class PersonaDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	public Persona getPersonaByCurp(String curp) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT P.ID, P.NOMBRE, P.APELLIDOPATERNO, P.APELLIDOMATERNO, P.FECHANACIMIENTO, P.CURP, "
				+ "P.RFC, P.SEXO, P.NACIONALIDAD, P.EMAIL, P.DOCUMENTOPROBATORIO, P.RENAPOVALIDACION, P.SATVALIDACION, "
				+ "P.FECHAREGISTRO, P.ULTIMOREGISTRO, P.ESTATUS, EF.ID, EF.DESCRIPCION, M.ID, M.DESCRIPCION  "
				+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M  WHERE P.MUNICIPIO = M.ID AND M.ENTIDADFEDERATIVA = EF.ID AND P.CURP = '");
		query.append(curp);
		query.append("'");
		
		Persona persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}

	
}

class PersonaRowMapper implements RowMapper<Persona> {
    @Override
    public Persona mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Persona persona = new Persona();
 
    	persona.setId(rs.getLong(1));
        persona.setNombre(rs.getString(2));
 
        return persona;
    }
}

