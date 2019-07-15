package issstep.afiliacion.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Persona;

@Component
public class PersonaDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	public Persona getPersonaByColumnaStringValor(String columna, String valor) {
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
		
		Persona persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	public Persona getPersonaById(long noControl) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT P.*, EF.NOMBRE AS ENTIDAD, M.NOMBRE AS MUNICIPIO, L.NOMBRE AS LOCALIDAD "
				+ "FROM PERSONA P, ENTIDADFEDERATIVA EF, MUNICIPIO M, LOCALIDAD L "
				+ "WHERE P.NOENTIDADFEDERATIVA = EF.NOENTIDADFEDERATIVA AND "
				+ "P.NOMUNICIPIO = M.NOMUNICIPIO AND "
				+ "P.NOMUNICIPIO = L.NOMUNICIPIO AND P.NOLOCALIDAD = L.NOLOCALIDAD AND P.NOCONTROL = ");
		
		
		query.append(noControl);
				
		System.out.println(query.toString());
		
		Persona persona = null;
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
	
	public void actualiza (Persona persona) {
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

class PersonaRowMapper implements RowMapper<Persona> {
    @Override
    public Persona mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Persona persona = new Persona();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoAfiliacion(rs.getLong("NOAFILIACION"));
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
        persona.setNoEntidad(rs.getLong("NOENTIDADFEDERATIVA"));
        persona.setEntidad(rs.getString("ENTIDAD"));
        persona.setNoMunicipio(rs.getLong("NOMUNICIPIO"));
        persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setNoLocalidad(rs.getLong("NOLOCALIDAD"));
        persona.setLocalidad(rs.getString("LOCALIDAD"));
        
        return persona;
    }
}

