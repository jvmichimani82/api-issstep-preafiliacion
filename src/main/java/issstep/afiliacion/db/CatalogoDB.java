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

import issstep.afiliacion.model.Descripcion;
import issstep.afiliacion.model.Documento;
import issstep.afiliacion.model.Catalogo;
import issstep.afiliacion.model.Clinica;
import issstep.afiliacion.model.Colonia;

@Component
public class CatalogoDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;
	
	@Autowired
	@Qualifier("afiliacionJdbcTemplate")
	private JdbcTemplate afiliacionDBTemplate;
	
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
		
		query.append("DELETE FROM TIPODOCTO WHERE id = " + id);
		
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
	
	public List<Catalogo> getMunicipios(long idEstado) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT CLAVEMUNICIPIO AS ID, DESCRIPCION FROM KMUNICIPIO WHERE CLAVEESTADO = " + idEstado);
		
		return getRegistros( query );
	}
	
	public List<Catalogo> getLocalidades(long idEstado, long idMunicipio) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT CLAVELOCALIDAD AS ID, DESCRIPCION FROM KLOCALIDAD WHERE CLAVEESTADO = " + idEstado);
		query.append(" AND CLAVEMUNICIPIO = " + idMunicipio);
		
		return getRegistros( query );
	}
	
	public List<Catalogo> getRegistros(StringBuilder query) {
		List<Catalogo> catalogo = null;
		try {
			catalogo =   afiliacionDBTemplate.query(query.toString(), new CatalogoRowMapper());
			return catalogo;
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Colonia> getColonias(long idEstado, long idMunicipio, long idLocalidad) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM KCOLONIA WHERE CLAVEESTADO = " + idEstado);
		query.append(" AND CLAVEMUNICIPIO = " + idMunicipio);
		query.append(" AND CLAVELOCALIDAD = " + idLocalidad);
		
		List<Colonia> colonia = null;
		
		try {
			colonia =   afiliacionDBTemplate.query(query.toString(), new ColoniaRowMapper());
			return colonia;
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Clinica> getClinicas() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM KCLINICASERVICIO");
		
		List<Clinica> clinica = null;
		
		try {
			clinica =   afiliacionDBTemplate.query(query.toString(), new ClinicaRowMapper());
			return clinica;
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

class CatalogoRowMapper implements RowMapper<Catalogo> {
    @Override
    public Catalogo mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	Catalogo catalogo= new Catalogo();
 
    	catalogo.setId(rs.getLong("ID"));
    	catalogo.setDescripcion(rs.getString("DESCRIPCION"));
    	
        return catalogo;
    }
}

class ColoniaRowMapper implements RowMapper<Colonia> {
	 @Override
	 public Colonia mapRow(ResultSet rs, int rowNum) throws SQLException {
		 Colonia colonia = new Colonia();
		 
		 colonia.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
		 colonia.setClaveColonia(rs.getLong("CLAVECOLONIA"));
		 colonia.setClaveEstado(rs.getLong("CLAVEESTADO"));
		 colonia.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
		 colonia.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
		 colonia.setCodigoPostal(rs.getLong("CODIGOPOSTAL"));
		 colonia.setDescripcion(rs.getString("DESCRIPCION"));
		 
		return colonia;
	 }
}

class ClinicaRowMapper implements RowMapper<Clinica> {
	 @Override
	 public Clinica mapRow(ResultSet rs, int rowNum) throws SQLException {
		 Clinica clinica = new Clinica();
		 
		 clinica.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
		 clinica.setNivel(rs.getLong("NIVEL"));
		 clinica.setDescripcion(rs.getString("DESCRIPCION"));
		 clinica.setRegionIssstep(rs.getLong("REGIONiSSSTEP"));
		 
		return clinica;
	 }
}
