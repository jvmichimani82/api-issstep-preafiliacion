package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import issstep.afiliacion.model.Archivo;
import issstep.afiliacion.model.DocumentosByParentesco;

@Component
public class ArchivoDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	
	public Archivo getArchivo(long claveDocumento) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM DOCUMENTO WHERE CLAVEDOCUMENTO = ");
		query.append(claveDocumento);
		
				
		Archivo archive = null;
		try {
			archive =  mysqlTemplate.queryForObject(query.toString(), new ArchivoRowMapper());
			return archive;
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * Regresamos la descripcion del tipo de documento por id
	 */
	
	public String getTipoArchivoByParentesco(long idParentesco, long idTipoArchivo) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT TA.DESCRIPCION AS ARCHIVO FROM KPARENTESCOTIPOARCHIVO P, KTIPOARCHIVO TA");
		query.append(" WHERE P.CLAVETIPOARCHIVO = TA.CLAVETIPOARCHIVO AND P.CLAVETIPOARCHIVO = ");
		query.append(idTipoArchivo);
		query.append(" AND P.CLAVEPARENTESCO = ");
		query.append(idParentesco);
		
		try {
			return (String) mysqlTemplate.queryForObject(query.toString(), String.class);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	
	public List<DocumentosByParentesco> getDocumentosByParentesco(long idParentesco) {
		StringBuilder query = new StringBuilder("SELECT P.*, TA.DESCRIPCION AS ARCHIVO FROM KPARENTESCOTIPOARCHIVO P, KTIPOARCHIVO TA ");
		query.append("WHERE P.CLAVETIPOARCHIVO = TA.CLAVETIPOARCHIVO AND P.CLAVEPARENTESCO = ");
		
		query.append(idParentesco);
		query.append(" ORDER BY ARCHIVO");
		
		System.out.println(query.toString());
		
		List<DocumentosByParentesco> documentos = new ArrayList<DocumentosByParentesco>();
		try {
			documentos =  mysqlTemplate.query(query.toString(), new ParentescoTipoArchivoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentos;
	}
	
	public long insertarArchivo (Archivo archivo) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO DOCUMENTO "
				+ "( NOCONTROL, NOPREAFILIACION, NOBENEFICIARIO, CLAVEPARENTESCO, "
				+ "CLAVETIPOARCHIVO, NOMBRE, URLARCHIVO, ESVALIDO, CLAVEUSUARIOREGISTRO, "
				+ "FECHAREGISTRO, CLAVEUSUARIOMODIFICACION, ESTATUS)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
	    	            pst.setLong(1, archivo.getNoControl());
	    	            pst.setLong(2, archivo.getNoPreAfiliacion());
	    	            pst.setLong(3, archivo.getNoBeneficiario());
	    	            pst.setLong(4, archivo.getClaveParentesco());
	    	            pst.setLong(5, archivo.getClaveTipoArchivo());
	    	            pst.setString(6, archivo.getNombre());
	    	            pst.setString(7, archivo.getUrlArchivo());
	    	            pst.setInt(8, archivo.getEsValido());
	    	            pst.setLong(9, archivo.getClaveUsuarioRegistro());
	    	            pst.setTimestamp(10, archivo.getFechaRegistro());
	    	            pst.setLong(11, archivo.getClaveUsuarioModificacion());
	    	            pst.setInt(12, archivo.getEstatus());

	    	            return pst;
	    	        }
	    	    },
	    	    keyHolder);
			
	    	return (Long)keyHolder.getKey();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public long delete (long claveDocumento) {
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM DOCUMENTO WHERE CLAVEDOCUMENTO = ? ");
			
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { claveDocumento });
			  return claveDocumento;
		} 
		catch (EmptyResultDataAccessException e) {
			return -1;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	
	
	public List<Archivo> getArchivos(long noControl, long noPreAfiliacion, long noBeneficiario, long claveParentesco) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM DOCUMENTO ");
		query.append(" WHERE NOCONTROL = ");
		query.append(noControl);
		query.append(" AND NOPREAFILIACION = ");
		query.append(noPreAfiliacion);
		query.append(" AND NOBENEFICIARIO  = ");
		query.append(noBeneficiario);
		query.append(" AND CLAVEPARENTESCO  = ");
		query.append(claveParentesco);
		
		List<Archivo> archivos = new ArrayList<Archivo>();
		try {
			archivos =  mysqlTemplate.query(query.toString(), new ArchivoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return archivos;
	}

    /* public long insertarUsuarioArchivo (long idUsuario, long idArchivo) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO IUSUARIODOCTO "
				+ "(DOCTO, USUARIO, ESTATUS)"
				+ " VALUES(?,?,?)");

		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
	    	            pst.setLong(1, idArchivo);
	    	            pst.setLong(2, idUsuario);
	    	            pst.setInt(3, 1);
	    	                return pst;
	    	        }
	    	    },
	    	    keyHolder);
			
	    	return idArchivo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	} */

    
	
}

class ArchivoRowMapper implements RowMapper<Archivo> {
    @Override
    public Archivo mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Archivo archive = new Archivo();
 
    	archive.setClaveDocumento(rs.getLong("claveDocumento"));
    	archive.setNoControl(rs.getLong("noControl"));
    	archive.setNoPreAfiliacion(rs.getLong("noPreAfiliacion"));
    	archive.setNoBeneficiario(rs.getLong("noBeneficiario"));
    	archive.setClaveParentesco(rs.getLong("claveParentesco"));
    	archive.setClaveTipoArchivo(rs.getLong("claveTipoArchivo"));
    	archive.setNombre(rs.getString("nombre"));
    	archive.setUrlArchivo(rs.getString("urlArchivo"));
    	archive.setEsValido(rs.getInt("esValido"));
    	archive.setClaveUsuarioRegistro(rs.getLong("claveUsuarioRegistro")); 
    	archive.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
    	archive.setClaveUsuarioModificacion(rs.getLong("claveUsuarioModificacion"));
 
        return archive;
    }
}

class ParentescoTipoArchivoRowMapper implements RowMapper<DocumentosByParentesco> {
    @Override
    public DocumentosByParentesco mapRow(ResultSet rs, int rowNum) throws SQLException {
    	DocumentosByParentesco parentescoTipoArchivo = new DocumentosByParentesco();
 
    	parentescoTipoArchivo.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
    	parentescoTipoArchivo.setClaveTipoArchivo(rs.getLong("CLAVETIPOARCHIVO"));
    	parentescoTipoArchivo.setEsObligatorio(rs.getLong("ESOBLIGATORIO"));
    	parentescoTipoArchivo.setArchivo(rs.getString("ARCHIVO"));
    	
        return parentescoTipoArchivo;
    }
}

