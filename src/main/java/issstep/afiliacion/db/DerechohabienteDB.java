package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.ActualizarDatos;
import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.InfoDerechohabiente;

@Component
public class DerechohabienteDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;
	
	@Autowired
	@Qualifier("afiliacionJdbcTemplate")
	private JdbcTemplate afiliacionDBTemplate;
	
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public Derechohabiente getPersonaByColumnaStringValor(String columna, String valor) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT DH.*, EF.DESCRIPCION AS ENTIDAD, M.DESCRIPCION AS MUNICIPIO, L.DESCRIPCION AS LOCALIDAD, C.DESCRIPCION AS COLONIA,"
				+ " EDOCVIL.DESCRIPCION AS ESTADOCIVIL, CSERV.DESCRIPCION AS CLINICA  "
					+ "FROM DERECHOHABIENTE DH, KESTADO EF, KMUNICIPIO M, KLOCALIDAD L, KCOLONIA AS C, KESTADOCIVIL EDOCVIL, KCLINICASERVICIO CSERV "
					+ "WHERE DH.CLAVEESTADO = EF.CLAVEESTADO AND "
					+ "DH.CLAVEMUNICIPIO = M.CLAVEMUNICIPIO AND "
					+ "DH.CLAVECOLONIA = C.CLAVECOLONIA AND "
					+ "DH.CLAVECLINICASERVICIO = CSERV.CLAVECLINICASERVICIO AND "
					+ "DH.CLAVEESTADOCIVIL = EDOCVIL.CLAVEESTADOCIVIL AND "
					+ "DH.CLAVEMUNICIPIO = L.CLAVEMUNICIPIO AND DH.CLAVELOCALIDAD = L.CLAVELOCALIDAD AND DH.");
		
		query.append(columna);
		query.append("= '");
		query.append(valor);
		query.append("'");
		
		System.out.println("Consulta ==> " + query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return persona;
	}
	
	//obtenemos el trabajador o derechoabiente de la bd de issstep
	
	public Derechohabiente getTrabajadorIssstepByColumnaStringValor(String columna, String valor) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT T.* FROM TRABAJADOR T "
					+ "WHERE T.");
		
		query.append(columna);
		query.append("= '");
		query.append(valor);
		query.append("'");
		
		System.out.println("Consulta ==> " + query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  afiliacionDBTemplate.queryForObject(query.toString(), new TrabajadorRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return persona;
	}
	
	
	public Derechohabiente getPersonaByNombre(Derechohabiente persona, HttpServletResponse response) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT DH.*, EF.DESCRIPCION AS ENTIDAD, M.DESCRIPCION AS MUNICIPIO, L.DESCRIPCION AS LOCALIDAD, C.DESCRIPCION AS COLONIA, EDOCVIL.DESCRIPCION AS ESTADOCIVIL, CSERV.DESCRIPCION AS CLINICA  "
					+ "FROM DERECHOHABIENTE DH, KESTADO EF, KMUNICIPIO M, KLOCALIDAD L, KCOLONIA AS C, KESTADOCIVIL EDOCVIL, KCLINICASERVICIO CSERV "
					+ "WHERE DH.CLAVEESTADO = EF.CLAVEESTADO AND "
					+ "DH.CLAVEMUNICIPIO = M.CLAVEMUNICIPIO AND "
					+ "DH.CLAVECOLONIA = C.CLAVECOLONIA AND "
					+ "DH.CLAVECLINICASERVICIO = CSERV.CLAVECLINICASERVICIO AND "
					+ "DH.CLAVEESTADOCIVIL = EDOCVIL.CLAVEESTADOCIVIL AND "
					+ "DH.CLAVEMUNICIPIO = L.CLAVEMUNICIPIO AND DH.CLAVELOCALIDAD = L.CLAVELOCALIDAD AND ");
		
		query.append("DH.PATERNO");
		query.append(" LIKE '%");
		query.append(persona.getPaterno());
		query.append("%'");
		query.append(" AND ");
		query.append("DH.MATERNO");
		query.append(" LIKE '%");
		query.append(persona.getMaterno());
		query.append("%'");
		query.append(" AND ");
		query.append("DH.NOMBRE");
		query.append(" LIKE '%");
		query.append(persona.getNombre());
		query.append("%'");
		query.append(" AND ");
		query.append("DH.CLAVEESTADO");
		query.append("= ");
		query.append(persona.getClaveEstado());
		query.append(" AND ");
		query.append("DH.FECHANACIMIENTO");
		query.append("= '");
		query.append(format.format(persona.getFechaNacimiento()));
		query.append("'");	
		System.out.println("Consulta ==> " + query.toString());
		
		
		Derechohabiente personaOld = null;
		try {
			personaOld =  mysqlTemplate.queryForObject(query.toString().toUpperCase(), new PersonaRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			response.setStatus(429);
			return null;
		}
		return personaOld;
	}
	
	public Derechohabiente getPersonaById(long noControl) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT DH.*, EF.DESCRIPCION AS ENTIDAD, M.DESCRIPCION AS MUNICIPIO, L.DESCRIPCION AS LOCALIDAD, C.DESCRIPCION AS COLONIA, EDOCVIL.DESCRIPCION AS ESTADOCIVIL, CSERV.DESCRIPCION AS CLINICA  "
				+ "FROM DERECHOHABIENTE DH, KESTADO EF, KMUNICIPIO M, KLOCALIDAD L, KCOLONIA AS C, KESTADOCIVIL EDOCVIL, KCLINICASERVICIO CSERV "
				+ "WHERE DH.CLAVEESTADO = EF.CLAVEESTADO AND "
				+ "DH.CLAVEMUNICIPIO = M.CLAVEMUNICIPIO AND "
				+ "DH.CLAVECOLONIA = C.CLAVECOLONIA AND "
				+ "DH.CLAVECLINICASERVICIO = CSERV.CLAVECLINICASERVICIO AND "
				+ "DH.CLAVEESTADOCIVIL = EDOCVIL.CLAVEESTADOCIVIL AND "
				+ "DH.CLAVEMUNICIPIO = L.CLAVEMUNICIPIO AND DH.CLAVELOCALIDAD = L.CLAVELOCALIDAD AND DH.NOCONTROL =");	
		
		query.append(noControl);
				
		System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
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
		query.append("UPDATE DERECHOHABIENTE SET NOMBRE= ?, PATERNO= ?, MATERNO = ?, EMAIL= ?, FECHAREGISTRO= ?, SITUACION= ?, CLAVEUSUARIOREGISTRO= ? WHERE NOCONTROL = ? ");
					
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					  persona.getNombre(), persona.getPaterno(), persona.getMaterno(), 
					  persona.getEmail(), persona.getFechaRegistro(), persona.getSituacion(), 
					  persona.getClaveUsuarioRegistro(),persona.getNoControl()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public long actualizaDatos(ActualizarDatos actualizarDatos) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE DERECHOHABIENTE SET DIRECCION = '"
					+ actualizarDatos.getDireccion()
					+ "', TELEFONOCASA = '"
					+ actualizarDatos.getTelefonoCasa() 
					+ "', TELEFONOCELULAR = '"
					+ actualizarDatos.getTelefonoCelular() 
					+ "' WHERE NOCONTROL = "
					+ actualizarDatos.getNoControl() 
					+ " AND NOPREAFILIACION = " 
					+ actualizarDatos.getNoPreAfiliacion());
					
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString());
			  return actualizarDatos.getNoControl();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}		
	}
	
	public long createDerechohabiente( Derechohabiente derechohabiente ) {
		return createOrDeleteDerechohabiente( derechohabiente, 0, "create");
	}
	
	public long deleteDerechohabiente(  long noControl ) {
		return createOrDeleteDerechohabiente( null, noControl,"delete");
	}
	
	public long createOrDeleteDerechohabiente(  Derechohabiente derechohabiente, long noControl, String opcion) {
		StringBuilder query = new StringBuilder();
		
		
		if (opcion.equals("create")) {
			StringBuilder queryUsuario = new StringBuilder();
				
			query.append("INSERT INTO DERECHOHABIENTE (NOCONTROL, NOPREAFILIACION, NOMBRE, PATERNO, MATERNO, "
					+ "EMAIL, FECHANACIMIENTO, SEXO, CURP, RFC, DIRECCION, "
					+ "CODIGOPOSTAL, TELEFONOCASA, TELEFONOCELULAR, FECHAPREAFILIACION, SITUACION, "
					+ "CLAVEUSUARIOREGISTRO, FECHAREGISTRO, CLAVEUSUARIOMODIFICACION, "
					+ "CLAVEESTADOCIVIL, CLAVECOLONIA, "
					+ "CLAVECLINICASERVICIO, CLAVELOCALIDAD, CLAVEMUNICIPIO, CLAVEESTADO) VALUES (" 
					+ derechohabiente.getNoControl() + ", " + derechohabiente.getNoPreAfiliacion()  
					+ ", '" + derechohabiente.getNombre() + "'" + ", '" + derechohabiente.getPaterno() + "'"
					+ ", '" + derechohabiente.getMaterno() + "'" + ", '" + derechohabiente.getEmail() + "', '"
					+ derechohabiente.getFechaNacimiento()+ "', '"  + derechohabiente.getSexo()
					+ "', '" + derechohabiente.getCurp() + "'" + ", '" + derechohabiente.getRfc() + "'"
					+ ", '" + derechohabiente.getDireccion() + "'" + ", '" + derechohabiente.getCodigoPostal() + "'"
					+ ", '" + derechohabiente.getTelefonoCasa() + "', '" + derechohabiente.getTelefonoCelular() + "', '"
					+ derechohabiente.getFechaPreAfiliacion() + "', "  + derechohabiente.getSituacion() + ", " 
					+ derechohabiente.getClaveUsuarioRegistro() + ", '"  + derechohabiente.getFechaRegistro() + "', " 
					+ derechohabiente.getClaveUsuarioModificacion() + ", "  
					+ (derechohabiente.getClaveEstadoCivil() > 0 ? derechohabiente.getClaveEstadoCivil() : null) + ", "  + derechohabiente.getClaveColonia() + ", " 
					+ derechohabiente.getClaveClinicaServicio() + ", "  + derechohabiente.getClaveLocalidad() + ", " 
					+ derechohabiente.getClaveMunicipio() + ", "  + derechohabiente.getClaveEstado() + ")" );
		}
		else 
			query.append("DELETE FROM DERECHOHABIENTE WHERE noControl = " + noControl);
		
		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"noControl"});
	    	            return pst;
	    	        }
	    	    },
	    	    keyHolder);
			
			return (opcion.equals("create")) ? (long) derechohabiente.getNoControl() : (long) 1;		
	    	
		} 
		catch (DataIntegrityViolationException e) {
			return (long) -1;
	    }
		catch (Exception e) {
			e.printStackTrace();
			return (long) 0;
		}
		
	}
	
	public List<InfoDerechohabiente> getDerechohabientesPorEstatusDeValidacion( int estatusValidacion) {
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT D.NOCONTROL, D.NOPREAFILIACION, D.NOMBRE, D.PATERNO, D.MATERNO, D.CURP, D.CLAVEUSUARIOREGISTRO FROM DERECHOHABIENTE D, "
				+ "(SELECT NOCONTROL, NOPREAFILIACION FROM DOCUMENTO WHERE ESVALIDO = " + estatusValidacion 
				+ " GROUP BY NOCONTROL, NOPREAFILIACION) DOC" 
				+ " WHERE D.NOCONTROL = DOC.NOCONTROL AND D.NOPREAFILIACION = DOC.NOPREAFILIACION");
		
		List<InfoDerechohabiente> lista = null;
		
		try {
			lista = mysqlTemplate.query(query.toString(), new ListaPersonaRowMapper());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<Derechohabiente> getBeneficiariosByDerechohabiente(long noControl) {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT DH.*, BE.NOBENEFICIARIO, BE.CLAVEPARENTESCO "
				+ "FROM DERECHOHABIENTE DH, "
				+ "BENEFICIARIO BE "
				+ "WHERE "
				+ "DH.NOCONTROL = BE.NOCONTROL AND DH.NOPREAFILIACION = BE.NOPREAFILIACION "
				+ "AND DH.NOCONTROL =");
		query.append(noControl);

		System.out.println("Parentescos ==> " + query.toString());
		List<Derechohabiente> beneficiarios = null;
		try {
			beneficiarios =  mysqlTemplate.query(query.toString(), new DerechohabienteRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return beneficiarios;
	}
	
	public List<Derechohabiente> getBeneficiariosByTrabajadorIssstep(long noControl) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT B.* "
				+ "FROM BENEFICIARIO B "
				+ "WHERE "
				+ "B.NOCONTROL =");	
		
		query.append(noControl);
		
		System.out.println(query.toString());
		List<Derechohabiente> beneficiarios = null;
		try {
			beneficiarios =  afiliacionDBTemplate.query(query.toString(), new BeneficiarioRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return beneficiarios;
	}
}



class PersonaRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setEstado(rs.getString("ENTIDAD"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setLocalidad(rs.getString("LOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setColonia(rs.getString("COLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClinicaServicio(rs.getString("CLINICA"));        
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        persona.setEstadoCivil(rs.getString("ESTADOCIVIL"));
        
        return persona;
    }   
}

class PersonaConParentescoRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setEstado(rs.getString("ENTIDAD"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setLocalidad(rs.getString("LOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setColonia(rs.getString("COLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClinicaServicio(rs.getString("CLINICA"));        
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        persona.setEstadoCivil(rs.getString("ESTADOCIVIL"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        persona.setParentesco(rs.getString("PARENTESCO"));
        
        return persona;
    }   
}

class TrabajadorRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DOMICILIO"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONO"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOCAPTURA"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        
        return persona;
    }
    
    
    
}

class BeneficiarioRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOBENEFICIARIO"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DOMICILIO"));
        persona.setTelefonoCasa(rs.getString("TELEFONO"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACIONB"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOCAPTURA"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        
        return persona;
    }
  
}

class DerechohabienteRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoBeneficiario(rs.getLong("NOBENEFICIARIO"));
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getTimestamp("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        
        return persona;
    }
    
    
    
}


class ListaPersonaRowMapper implements RowMapper<InfoDerechohabiente> {
    @Override
    public InfoDerechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	InfoDerechohabiente infoDerechohabiente = new InfoDerechohabiente();
 
    	infoDerechohabiente.setNoControl(rs.getLong("NOCONTROL"));
    	infoDerechohabiente.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
    	infoDerechohabiente.setNombre(rs.getString("NOMBRE"));
    	infoDerechohabiente.setPaterno(rs.getString("PATERNO"));
    	infoDerechohabiente.setMaterno(rs.getString("MATERNO"));
        infoDerechohabiente.setCurp(rs.getString("CURP"));
        infoDerechohabiente.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
           
        return infoDerechohabiente;
    }
}

