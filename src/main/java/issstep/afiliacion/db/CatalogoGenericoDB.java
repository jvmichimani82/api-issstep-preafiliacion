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
import issstep.afiliacion.model.Descripcion;

@Component
public class CatalogoGenericoDB {
	
	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;
	
	public List<CatalogoGenerico> getRegistros( String catalogo ) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ID, DESCRIPCION FROM " + catalogo + " WHERE ESTATUS = 1");
		
		 List<CatalogoGenerico> documentos = null;
		try {
			documentos = mysqlTemplate.query(query.toString(), new RegistroRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentos;
	}
	
	public CatalogoGenerico getRegistro( String catalogo, long id ) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ID, DESCRIPCION FROM "+ catalogo + " WHERE ESTATUS = 1 AND ID = " + id);
		
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
		
		System.out.println(registro);
		return registro;
	}
	
	public int updateRegistro( String catalogo, CatalogoGenerico registro ) {
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE " + catalogo + " SET descripcion = '" + registro.getDescripcion() + "' WHERE id = ?");
		System.out.println(query.toString());
		
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
		else 
			query.append("DELETE FROM " + catalogo + " WHERE id = " + id);
		
		System.out.println(query.toString());
		
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
