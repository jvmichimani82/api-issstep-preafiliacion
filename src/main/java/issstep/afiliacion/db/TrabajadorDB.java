package issstep.afiliacion.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Derechohabiente;

@Component
public class TrabajadorDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	public Derechohabiente getPersonaByColumnaStringValor(String columna, String valor) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT P.*, EF.NOMBRE AS ENTIDAD, M.NOMBRE AS MUNICIPIO, L.NOMBRE AS LOCALIDAD "
						+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M, LOCALIDAD L "
						+ "WHERE P.NOENTIDADFEDERATIVA = EF.NOENTIDADFEDERATIVA AND "
						+ "P.NOMUNICIPIO = M.NOMUNICIPIO AND "
						+ "P.NOMUNICIPIO = L.NOMUNICIPIO AND P.NOLOCALIDAD = L.NOLOCALIDAD AND P.");
		
		query.append(columna);
		query.append("= '");
		query.append(valor);
		query.append("'");
		
		System.out.println("Consulta ==> " + query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	public Derechohabiente getPersonaById(long noControl) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT P.*, EF.NOMBRE AS ENTIDAD, M.NOMBRE AS MUNICIPIO, L.NOMBRE AS LOCALIDAD "
				+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M, LOCALIDAD L "
				+ "WHERE P.NOENTIDADFEDERATIVA = EF.NOENTIDADFEDERATIVA AND "
				+ "P.NOMUNICIPIO = M.NOMUNICIPIO AND "
				+ "P.NOMUNICIPIO = L.NOMUNICIPIO AND P.NOLOCALIDAD = L.NOLOCALIDAD AND P.NOCONTROL = ");
		
		
		query.append(noControl);
				
		System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	/*public Persona getPersonaByCurp(String curp) {
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
	
	public Persona getPersonaByEmail(String mail) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT P.ID AS IDPERSONA, P.NOMBRE, P.APELLIDOPATERNO, P.APELLIDOMATERNO, P.FECHANACIMIENTO, P.CURP, "
				+ "P.RFC, P.SEXO, P.NACIONALIDAD, P.EMAIL, P.DOCUMENTOPROBATORIO, P.RENAPOVALIDACION, P.SATVALIDACION, "
				+ "P.FECHAREGISTRO, P.ULTIMOREGISTRO, P.ESTATUS, EF.ID AS IDENTIDAD, EF.DESCRIPCION AS DESENTIDAD, M.ID AS IDMUN, M.DESCRIPCION AS DESMUN  "
				+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M  WHERE P.MUNICIPIO = M.ID AND M.ENTIDADFEDERATIVA = EF.ID AND P.EMAIL = '");
		query.append(mail);
		query.append("'");
		
		System.out.println(query.toString());
		
		Persona persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}*/
	
	public void actualiza (Derechohabiente persona) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE PERSONA SET NOMBRE= ?, PATERNO= ?, MATERNO = ?, EMAIL= ?, FECHAREGISTRO= ?, SITUACION= ? WHERE NOCONTROL = ? ");
					
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					  persona.getNombre(), persona.getPaterno(), persona.getMaterno(), 
					  persona.getEmail(), persona.getFechaModificacion(), persona.getSituacion(), persona.getNoControl()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
}

class PersonaRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveEstado(rs.getLong("NOENTIDADFEDERATIVA"));
        persona.setEstado(rs.getString("ENTIDAD"));
        persona.setClaveMunicipio(rs.getLong("NOMUNICIPIO"));
        persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("NOLOCALIDAD"));
        persona.setLocalidad(rs.getString("LOCALIDAD"));
        
        return persona;
    }
}

