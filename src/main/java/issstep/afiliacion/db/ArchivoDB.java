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
import issstep.afiliacion.model.ParentescoTipoArchivo;

@Component
public class ArchivoDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	
	public Archivo getArchivoById(long id) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT D.ID, D.TIPODOCTO, TD.DESCRIPCION AS DESTIPODOCTO, D.URLDOCTO, D.NOMBREDOCTO, D.FECHAREGISTRO, D.ESTATUS "
				+ "FROM DOCTO D, TIPODOCTO TD WHERE D.TIPODOCTO = TD.ID AND D.ID = ");
		query.append(id);
	
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
	
	public String getTipoArchivoByParentescoAndId(long idParentesco, long id) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT TA.NOMBRE AS ARCHIVO FROM PARENTESCOTARCHIVO P, TIPOARCHIVO TA");
		query.append(" WHERE P.NOTARCHIVO = ");
		query.append(id);
		query.append(" AND NOPARENTESCO = ");
		query.append(idParentesco);
		
		try {
			return (String) mysqlTemplate.queryForObject(query.toString(), String.class);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Archivo> getArchivosByUsuario(long idIsuario) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT D.ID, D.TIPODOCTO, TD.DESCRIPCION AS DESTIPODOCTO, D.URLDOCTO, D.NOMBREDOCTO, D.FECHAREGISTRO, D.ESTATUS "
				+ "FROM DOCTO D, TIPODOCTO TD WHERE D.TIPODOCTO = TD.ID AND D.ID IN"
				+ "(SELECT MAX(D.ID) AS ID " + 
				"FROM DOCTO D, TIPODOCTO TD, IUSUARIODOCTO IUD " + 
				"WHERE D.TIPODOCTO = TD.ID " + 
				"AND D.ID = IUD.DOCTO " + 
				"AND IUD.USUARIO = ");
		
		query.append(idIsuario);
		query.append(" GROUP BY D.TIPODOCTO)");
		
		List<Archivo> archivos = new ArrayList<Archivo>();
		try {
			archivos =  mysqlTemplate.query(query.toString(), new ArchivoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return archivos;
	}
	
	
	public List<Archivo> getArchivosByParentesco(long idParentesco) {
		StringBuilder query = new StringBuilder("SELECT P.*, TA.NOMBRE AS ARCHIVO FROM PARENTESCOTARCHIVO P, TIPOARCHIVO TA ");
		query.append("WHERE P.NOTARCHIVO = TA.NOARCHIVO AND NOPARENTESCO =");
		
		query.append(idParentesco);
		query.append(" ORDER BY ARCHIVO)");
		
		List<Archivo> archivos = new ArrayList<Archivo>();
		try {
			archivos =  mysqlTemplate.query(query.toString(), new ArchivoRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return archivos;
	}
	
	public long insertarArchivo (Archivo archivo) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO DOCTO "
				+ "(TIPODOCTO, URLDOCTO, NOMBREDOCTO, FECHAREGISTRO, ESTATUS)"
				+ " VALUES(?,?,?,?,?)");

		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"id"});
	    	            //pst.setLong(1, archivo.getTipoDocto());
	    	            //pst.setString(2, archivo.getUrlDocto());
	    	            //pst.setString(3, archivo.getNombreDocto());
	    	            pst.setTimestamp(4, archivo.getFechaRegistro());
	    	            //pst.setInt(5, archivo.getEstatus());
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
	
	public long insertarUsuarioArchivo (long idUsuario, long idArchivo) {
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
	
	
}

class ArchivoRowMapper implements RowMapper<Archivo> {
    @Override
    public Archivo mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Archivo archive = new Archivo();
 
    	/*archive.setId(rs.getLong("ID"));
    	archive.setNombreDocto(rs.getString("NOMBREDOCTO"));
    	archive.setTipoDocto(rs.getInt("TIPODOCTO"));
    	archive.setDesTipoDocto(rs.getString("DESTIPODOCTO"));
        archive.setUrlDocto(rs.getString("URLDOCTO"));*/
        archive.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        archive.setActivo(rs.getInt("ACTIVO"));
       
 
        return archive;
    }
}

class ParentescoTipoArchivoRowMapper implements RowMapper<ParentescoTipoArchivo> {
    @Override
    public ParentescoTipoArchivo mapRow(ResultSet rs, int rowNum) throws SQLException {
    	ParentescoTipoArchivo parentescoTipoArchivo = new ParentescoTipoArchivo();
 
    	parentescoTipoArchivo.setNoTArchivo(rs.getLong("NOTARCHIVO"));
    	parentescoTipoArchivo.setObligatorio(rs.getLong("OBLIGATORIO"));
    	parentescoTipoArchivo.setArchivo(rs.getString("ARCHIVO"));
    	
        return parentescoTipoArchivo;
    }
}

