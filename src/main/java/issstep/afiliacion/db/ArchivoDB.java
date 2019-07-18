package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	
	public Archivo getArchivo(long noTrabajador, long noBeneficiario, long noParentesco, long noTArchivo) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM TBARCHIVO WHERE NOTRABAJADOR = ");
		query.append(noTrabajador);
		query.append(" AND NOBENEFICIARIO = ");
		query.append(noBeneficiario);
		query.append(" AND NOPARENTESCO = ");
		query.append(noParentesco);
		query.append(" AND NOTARCHIVO = ");
		query.append(noTArchivo);
				
		Archivo archive = null;
		try {
			archive =  mysqlTemplate.queryForObject(query.toString(), new ArchivoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return archive;
	}
	
	
	/*
	 * Regresamos la descripcion del tipo de documento por id
	 */
	
	public String getTipoArchivoByParentesco(long idParentesco, long idTipoArchivo) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT TA.NOMBRE AS ARCHIVO FROM PARENTESCOTARCHIVO P, TIPOARCHIVO TA");
		query.append(" WHERE P.NOTARCHIVO = TA.NOARCHIVO AND P.NOTARCHIVO = ");
		query.append(idTipoArchivo);
		query.append(" AND NOPARENTESCO = ");
		query.append(idParentesco);
		
		try {
			return (String) mysqlTemplate.queryForObject(query.toString(), String.class);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	
	public List<DocumentosByParentesco> getDocumentosByParentesco(long idParentesco) {
		StringBuilder query = new StringBuilder("SELECT P.*, TA.NOMBRE AS ARCHIVO FROM PARENTESCOTARCHIVO P, TIPOARCHIVO TA ");
		query.append("WHERE P.NOTARCHIVO = TA.NOARCHIVO AND NOPARENTESCO =");
		
		query.append(idParentesco);
		query.append(" ORDER BY ARCHIVO");
		
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
		query.append("INSERT INTO TBARCHIVO "
				+ "(NOTRABAJADOR, NOBENEFICIARIO, NOPARENTESCO, NOTARCHIVO, NOMBRE, URLARCHIVO, VALIDADO, FECHAREGISTRO, ACTIVO)"
				+ " VALUES(?,?,?,?,?,?,?,?)");

		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
	    	            pst.setLong(1, archivo.getNoTrabajador());
	    	            pst.setLong(2, archivo.getNoBeneficiario());
	    	            pst.setLong(3, archivo.getNoParentesco());
	    	            pst.setLong(4, archivo.getNoTArchivo());
	    	            pst.setString(5, archivo.getNombre());
	    	            pst.setString(6, archivo.getUrlArchivo());
	    	            pst.setInt(7, archivo.getValidado());
	    	            pst.setTimestamp(8, archivo.getFechaRegistro());
	    	            pst.setInt(9, archivo.getActivo());
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
	
	public void delete (Archivo archivo) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE USUARIO SET ROL = ?, PASSWORD = ?, TOKEN = ?, ULTIMOREGISTRO = ?, ESTATUS = ? WHERE USUARIO = ? ");
			
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public List<Archivo> getArchivos(long idTrabajador, long idBeneficiario, long idParentesco) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM TBARCHIVO ");
		query.append(" WHERE NOTRABAJADOR = ");
		query.append(idTrabajador);
		query.append(" AND NOBENEFICIARIO = ");
		query.append(idBeneficiario);
		query.append(" AND NOPARENTESCO  = ");
		query.append(idParentesco);
		
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
 
    	archive.setNoTrabajador(rs.getLong("NOTRABAJADOR"));
    	archive.setNoBeneficiario(rs.getLong("NOBENEFICIARIO"));
    	archive.setNoParentesco(rs.getLong("NOPARENTESCO"));
    	archive.setNoTArchivo(rs.getLong("NOTARCHIVO"));
    	archive.setNombre(rs.getString("NOMBRE"));
    	archive.setUrlArchivo(rs.getString("URLARCHIVO"));
    	archive.setValidado(rs.getInt("VALIDADO"));
    	archive.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
    	archive.setActivo(rs.getInt("ACTIVO"));
       
 
        return archive;
    }
}

class ParentescoTipoArchivoRowMapper implements RowMapper<DocumentosByParentesco> {
    @Override
    public DocumentosByParentesco mapRow(ResultSet rs, int rowNum) throws SQLException {
    	DocumentosByParentesco parentescoTipoArchivo = new DocumentosByParentesco();
 
    	parentescoTipoArchivo.setNoTArchivo(rs.getLong("NOTARCHIVO"));
    	parentescoTipoArchivo.setObligatorio(rs.getLong("OBLIGATORIO"));
    	parentescoTipoArchivo.setArchivo(rs.getString("ARCHIVO"));
    	
        return parentescoTipoArchivo;
    }
}

