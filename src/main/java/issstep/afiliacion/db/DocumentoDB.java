package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;

@Component
public class DocumentoDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	public List<Documento> getDocumentos() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ID, DESCRIPCION FROM TIPODOCTO WHERE ESTATUS = 1");
		
		 List<Documento> documentos = null;
		try {
			documentos =   mysqlTemplate.query(query.toString(), new DocumentoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentos;
	}

	public Documento getDocumento(long id) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ID, DESCRIPCION FROM TIPODOCTO WHERE ESTATUS = 1 AND ID = ");
		query.append(id);

		Documento documento = null;
		try {
			documento = mysqlTemplate.queryForObject(query.toString(), new DocumentoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return documento;
	}
	
	public int updateDocumento(Documento documento) {
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE TIPODOCTO SET descripcion = '" + documento.getDescripcion() + "' WHERE id = ?");
		System.out.println(query.toString());
		
		int numDocumento = 0;
		try {
			numDocumento = mysqlTemplate.update(query.toString(), new Object[]{documento.getId()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return numDocumento;
	}
	
	public long createDocumento(Descripcion descripcion) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO TIPODOCTO (DESCRIPCION, ESTATUS) VALUES ('" + descripcion.getDescripcion() + "', 1)");
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
			
	    	return (long) keyHolder.getKey();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (long) 0;
		
	}
	
	public long deleteDocumento(long id) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM TIPODOCTO WHERE id = ?");
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
			
	    	return (long) 1;
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (long) 0;		
	}
	
}

class DocumentoRowMapper implements RowMapper<Documento> {
    @Override
    public Documento mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	Documento documento= new Documento();
 
    	documento.setId(rs.getLong("ID"));
    	documento.setDescripcion(rs.getString("DESCRIPCION"));
    	
        return documento;
    }
}

