package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.CatalogoGenerico;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Descripcion;

@Component
public class CatalogoGenericoDB {
	
	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	JdbcTemplate mysqlTemplate;
	
	@Autowired
	@Qualifier("afiliacionJdbcTemplate")
	JdbcTemplate afiliacionDBTemplate;
	
	boolean esDatoLocal;
	
	public String getNombreId( String catalogo ) {
		String nombreId = "";
		esDatoLocal = false;
		
		switch (catalogo) {
			case "KESTADO": 	 nombreId = "CLAVEESTADO"; break;
			case "KESTADOCIVIL": nombreId = "CLAVEESTADOCIVIL"; break;
			case "KESTATUS": 	 nombreId = "CLAVEESTATUS"; esDatoLocal = true; break;
			case "KPARENTESCO":  nombreId = "CLAVEPARENTESCO"; esDatoLocal = true; break;
			case "KREGION": 	 nombreId = "CLAVEREGION"; break;
			case "KROL": 		 nombreId = "CLAVEROL"; esDatoLocal = true; break;
			case "KSITUACION":   nombreId = "CLAVESITUACION"; break;
			case "KTIPOARCHIVO": nombreId = "CLAVETIPOARCHIVO"; esDatoLocal = true; break;
		}
		
		return nombreId;
	}
	
	
	public List<CatalogoGenerico> getRegistros( String catalogo ) {
		String nombreId = getNombreId(catalogo);
		
		if (nombreId == "")
			return null;
					
		StringBuilder query = new StringBuilder();
		query.append("SELECT " + nombreId + " AS ID, DESCRIPCION FROM " + catalogo);
		
		List<CatalogoGenerico> documentos = null;
		try {
			if (esDatoLocal)
				documentos = mysqlTemplate.query(query.toString(), new RegistroRowMapper());
			else
				documentos = afiliacionDBTemplate.query(query.toString(), new RegistroRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentos;
	}
	
	public CatalogoGenerico getRegistro( String catalogo, long id ) {
		String nombreId = getNombreId(catalogo);
		
		if (nombreId == "")
			return null;
		
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT " + nombreId + " AS ID, DESCRIPCION FROM "+ catalogo + " WHERE " + nombreId + "= " + id);
		
		CatalogoGenerico registro = null;
		try {
			if (esDatoLocal)
				registro = mysqlTemplate.queryForObject(query.toString(), new RegistroRowMapper());
			else
				registro = afiliacionDBTemplate.queryForObject(query.toString(), new RegistroRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return registro;
	}
	
	public long getRegistroByDescripcion( String catalogo, String descripcion ) {
		String nombreId = getNombreId(catalogo);
		
		if (nombreId == "")
			return -1;
		
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT " + nombreId + " AS ID, DESCRIPCION FROM "+ catalogo + " WHERE DESCRIPCION = '" + descripcion + "'");
		
		CatalogoGenerico registro = null;
		try {
			if (esDatoLocal)
				registro = mysqlTemplate.queryForObject(query.toString(), new RegistroRowMapper());
			else
				registro = afiliacionDBTemplate.queryForObject(query.toString(), new RegistroRowMapper());
			return registro.getId();
		}
		catch (EmptyResultDataAccessException e) {
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
		
	}
	
	public String getDescripcionCatalogo( String catalogo, Derechohabiente derechohabiente) {
		String nombreId = getNombreId(catalogo);
		
		StringBuilder query = new StringBuilder();
		
		switch (catalogo) {
			case "KESTADO":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTADO = " + derechohabiente.getClaveEstado());
				break;
			case "KESTADOCIVIL":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTADOCIVIL = " + derechohabiente.getClaveEstadoCivil());
				break;
			case "KMUNICIPIO":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTADO = " + derechohabiente.getClaveEstado());
				query.append(" AND CLAVEMUNICIPIO = " + derechohabiente.getClaveMunicipio());
				break;
			case "KLOCALIDAD":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTADO = " + derechohabiente.getClaveEstado());
				query.append(" AND CLAVEMUNICIPIO = " + derechohabiente.getClaveMunicipio());
				query.append(" AND CLAVELOCALIDAD = " + derechohabiente.getClaveLocalidad());
				break;
			case "KCOLONIA":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTADO = " + derechohabiente.getClaveEstado());
				query.append(" AND CLAVEMUNICIPIO = " + derechohabiente.getClaveMunicipio());
				query.append(" AND CLAVELOCALIDAD = " + derechohabiente.getClaveLocalidad());
				query.append(" AND CLAVECOLONIA = " + derechohabiente.getClaveColonia());
				break;
			case "KCLINICASERVICIO":
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVECLINICASERVICIO = " + derechohabiente.getClaveClinicaServicio());
				break;
			case "KSITUACION": 
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVESITUACION = " + derechohabiente.getSituacion());
				break;
			case "KPARENTESCO": 
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEPARENTESCO = " + derechohabiente.getClaveParentesco());
				break;
			case "KESTATUS": 
				query.append("SELECT DESCRIPCION FROM "+ catalogo + " WHERE CLAVEESTATUS = " + derechohabiente.getEstatus());
				break;
		}
		
	
		// System.out.println(query.toString());
		
		CatalogoGenerico descripcion = null;
		try {
			if (esDatoLocal)
				descripcion = mysqlTemplate.queryForObject(query.toString(), new DescripcionRowMapper());
			else
				descripcion = afiliacionDBTemplate.queryForObject(query.toString(), new DescripcionRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return descripcion.getDescripcion();
	}
	
	public String getDescripcionParentesco( long claveParentesco ) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT DESCRIPCION FROM KPARENTESCO WHERE CLAVEPARENTESCO = " + claveParentesco);
		
		CatalogoGenerico descripcion = null;
		try {
			descripcion = mysqlTemplate.queryForObject(query.toString(), new DescripcionRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return descripcion.getDescripcion();		
	}
	
	public int updateRegistro( String catalogo, CatalogoGenerico registro ) {
		String nombreId = getNombreId(catalogo);
		
		if (nombreId == "")
			return -1;
		
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE " + catalogo + " SET descripcion = '" + registro.getDescripcion() + "' WHERE " + nombreId + " = ?");
		
		int idRegistro = 0;
		try {
			if (esDatoLocal)			
				idRegistro  = mysqlTemplate.update(query.toString(), new Object[]{registro.getId()});
			else
				idRegistro  = afiliacionDBTemplate.update(query.toString(), new Object[]{registro.getId()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return idRegistro ;
	}
	
	public long createRegistro( String catalogo, Descripcion descripcion ) {
		return createOrDeleteDocumento( catalogo, descripcion.getDescripcion(), 0, "create");
	}
	
	public long deleteRegistro( String catalogo, long id ) {
		return createOrDeleteDocumento( catalogo, "", id, "delete");
	}
	
	public long createOrDeleteDocumento( String catalogo, String descripcion, long id, String opcion) {		
		String nombreId = getNombreId(catalogo);
		
		StringBuilder query = new StringBuilder();
		
		if (opcion.equals("create")) 
			if (catalogo.equals("KPARENTESCO") || catalogo.equals("KROL") || catalogo.equals("KTIPOARCHIVO"))
				query.append("INSERT INTO " + catalogo + " (DESCRIPCION, ESTATUS) VALUES ('" + descripcion + "', 1)");
			else 
				query.append("INSERT INTO " + catalogo + " (DESCRIPCION) VALUES ('" + descripcion + "')");
		else 
			query.append("DELETE FROM " + catalogo + " WHERE " + nombreId + " = " + id);
		
		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			if (esDatoLocal)
				mysqlTemplate.update(
		    	    new PreparedStatementCreator() {
		    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
		    	            return pst;
		    	        }
		    	    },
		    	    keyHolder);
	    	else     
				afiliacionDBTemplate.update(
		    	    new PreparedStatementCreator() {
		    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
		    	            return pst;
		    	        }
		    	    },
		    	    keyHolder);
			
			return (opcion.equals("create")) ? (long) keyHolder.getKey() : (long) 1;		
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (long) 0;
		
	}	
}

class RegistroRowMapper implements RowMapper<CatalogoGenerico> {
    @Override
    public CatalogoGenerico mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	CatalogoGenerico registro= new CatalogoGenerico();
 
    	registro.setId(rs.getLong("ID"));
    	registro.setDescripcion(rs.getString("DESCRIPCION"));
    	
        return registro;
    }
}

class DescripcionRowMapper implements RowMapper<CatalogoGenerico> {
    @Override
    public CatalogoGenerico mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	CatalogoGenerico registro= new CatalogoGenerico();
    	registro.setDescripcion(rs.getString("DESCRIPCION"));
    	
        return registro;
    }
}

