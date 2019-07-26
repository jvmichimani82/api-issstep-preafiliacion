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
	private JdbcTemplate mysqlTemplate;
	
	public String getNombreId( String catalogo ) {
		String nombreId = "";
		
		switch (catalogo) {
			case "KESTADO": 	 nombreId = "CLAVEESTADO"; break;
			case "KESTADOCIVIL": nombreId = "CLAVEESTADOCIVIL"; break;
			case "KESTATUS": 	 nombreId = "CLAVEESTATUS"; break;
			case "KPARENTESCO":  nombreId = "CLAVEPARENTESCO"; break;
			case "KREGION": 	 nombreId = "CLAVEREGION"; break;
			case "KROL": 		 nombreId = "CLAVEROL"; break;
			case "KSITUACION":   nombreId = "CLAVESITUACION"; break;
			case "KTIPOARCHIVO": nombreId = "CLAVETIPOARCHIVO"; break;
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
			documentos = mysqlTemplate.query(query.toString(), new RegistroRowMapper());
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
			registro = mysqlTemplate.queryForObject(query.toString(), new RegistroRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return registro;
	}
	
	public String getDescripcionCatalogo( String catalogo, Derechohabiente derechohabiente) {
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
		}
		
	
		System.out.println(query.toString());
		
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
			idRegistro  = mysqlTemplate.update(query.toString(), new Object[]{registro.getId()});
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
		StringBuilder query = new StringBuilder();
		
		if (opcion.equals("create"))
			query.append("INSERT INTO " + catalogo + " (DESCRIPCION, ESTATUS) VALUES ('" + descripcion + "', 1)");
		else {
			String nombreId = getNombreId(catalogo);
			
			if (nombreId == "")
				return -1;
			
			query.append("DELETE FROM " + catalogo + " WHERE " + nombreId + " = " + id);
		}
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
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

