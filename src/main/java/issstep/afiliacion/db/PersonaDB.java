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
		query.append("SELECT P.ID AS IDPERSONA, P.NOMBRE, P.APELLIDOPATERNO, P.APELLIDOMATERNO, P.FECHANACIMIENTO, P.CURP, "
				+ "P.RFC, P.SEXO, P.NACIONALIDAD, P.EMAIL, P.DOCUMENTOPROBATORIO, P.RENAPOVALIDACION, P.SATVALIDACION, "
				+ "P.FECHAREGISTRO, P.ULTIMOREGISTRO, P.ESTATUS, EF.ID AS IDENTIDAD, EF.DESCRIPCION AS DESENTIDAD, M.ID AS IDMUN, M.DESCRIPCION AS DESMUN  "
				+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M  WHERE P.MUNICIPIO = M.ID AND M.ENTIDADFEDERATIVA = EF.ID AND P.CURP = '");
		query.append(curp);
		query.append("'");
		
		System.out.println(query.toString());
		
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
 
    	persona.setId(rs.getLong("IDPERSONA"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setApellidoPaterno("APELLIDOPATERNO");
        persona.setApellidoMaterno("APELLIDOMATERNO");
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setNacionalidad(rs.getString("NACIONALIDAD"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setDocumentoProbatorio(rs.getString("DOCUMENTOPROBATORIO"));
        persona.setRenapoValidacion(rs.getInt("RENAPOVALIDACION")==1?true:false);
        persona.setSatValidacion(rs.getInt("SATVALIDACION")==1?true:false);
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setUltimaModificacion(rs.getTimestamp("ULTIMOREGISTRO"));
        persona.setEstatus(rs.getInt("ESTATUS"));
        persona.setEntidad(rs.getLong("IDENTIDAD"));
        persona.setEntitadDes(rs.getString("DESENTIDAD"));
        persona.setMunicipio(rs.getLong("IDMUN"));
        persona.setMunicipioDesc(rs.getString("DESMUN"));
        return persona;
    }
}

