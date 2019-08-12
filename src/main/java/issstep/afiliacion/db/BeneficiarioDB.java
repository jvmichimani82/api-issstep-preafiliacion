package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.model.Derechohabiente;

@Component
public class BeneficiarioDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	
	public Beneficiario getBeneficiario(long noControl, long noPreAfiliacion, long claveParentesco) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM WBENEFICIARIO WHERE NOCONTROL = ");
		query.append(noControl);
		query.append(" AND NOPREAFILIACION= ");
		query.append(noPreAfiliacion);
		query.append(" AND CLAVEPARENTESCO = ");
		query.append(claveParentesco);
		
		// System.out.println("getBeneficiario == " + query.toString());
				
		// Beneficiario beneficiario = null;
		try {
			return  mysqlTemplate.queryForObject(query.toString(), new BeneficiarioRowMapper());
		} 
		
		catch (EmptyResultDataAccessException e) {
				return null;
			}		
		catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
	}
	
	public Beneficiario getBeneficiarioById(long idBeneficiario) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM WBENEFICIARIO WHERE NOBENEFICIARIO = ");
		query.append(idBeneficiario);
		
		// System.out.println("getBeneficiario == " + query.toString());
				
		// Beneficiario beneficiario = null;
		try {
			return  mysqlTemplate.queryForObject(query.toString(), new BeneficiarioRowMapper());
		} 
		
		catch (EmptyResultDataAccessException e) {
				return null;
			}		
		catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
	}
	
	public long createBeneficiario(long noControlTitular, Derechohabiente derechohabiente, long claveParentesco ) {
		return createOrDeleteDerechohabiente( noControlTitular, derechohabiente, claveParentesco, 0, "create");
	}
	
	public long deleteBeneficiario(long noBeneficiario ) {
		return createOrDeleteDerechohabiente( 0, null, 0, noBeneficiario,"delete");
	}
	
	public long createOrDeleteDerechohabiente( long noControlTitular, Derechohabiente derechohabiente, long claveParentesco, long noBeneficiario, String opcion) {
		StringBuilder query = new StringBuilder();
		
		
		if (opcion.equals("create")) 			
			query.append("INSERT INTO WBENEFICIARIO "
					+ "(NOCONTROL, NOPREAFILIACION, CLAVEPARENTESCO, FECHAAFILIACION, SITUACION, CLAVEUSUARIOREGISTRO, FECHAREGISTRO) VALUES("
					+ noControlTitular + ", " +  derechohabiente.getNoPreAfiliacion() + ", "
					+ claveParentesco + ", '" + derechohabiente.getFechaPreAfiliacion() + "', 1, " +
					+ derechohabiente.getClaveUsuarioRegistro() + ", '" + derechohabiente.getFechaRegistro() 
					+ "')" );
		else 
			query.append("DELETE FROM BENEFICIARIO WHERE NOBENEFICIARIO = " + noBeneficiario);
		
		// System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"noBeneficiario"});
	    	            return pst;
	    	        }
	    	    },
	    	    keyHolder);
			
			return (opcion.equals("create")) ? (long) keyHolder.getKey() : (long) 1;		
	    	
		} 
		catch (DataIntegrityViolationException e) {
			return (long) -1;
	    }
		catch (Exception e) {
			e.printStackTrace();
			return (long) 0;
		}
		
	}
	
	class BeneficiarioRowMapper implements RowMapper<Beneficiario> {
	    @Override
	    public Beneficiario mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Beneficiario beneficiario = new Beneficiario();
	 
	    	beneficiario.setNoBeneficiario(rs.getLong("noBeneficiario"));
	    	beneficiario.setNoControl(rs.getLong("noControl"));
	    	beneficiario.setNoPreAfiliacion(rs.getLong("noPreAfiliacion"));
	    	beneficiario.setClaveParentesco(rs.getLong("claveParentesco"));
	    	beneficiario.setFechaAfiliacion(rs.getTimestamp("fechaAfiliacion"));
	    	beneficiario.setSituacion(rs.getLong("situacion"));
	    	beneficiario.setClaveUsuarioRegistro(rs.getLong("claveUsuarioRegistro"));
	    	beneficiario.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
	    	beneficiario.setClaveUsuarioModificacion(rs.getLong("claveUsuarioModificacion"));    
	    	
	        return beneficiario;
	    }
	}
	
}

