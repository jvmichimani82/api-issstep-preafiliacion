package issstep.afiliacion.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Beneficiario;

@Component
public class BeneficiarioDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	
	public Beneficiario getBeneficiario(long noControl, long noPreAfiliacion, long claveParentesco) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM BENEFICIARIO WHERE NOCONTROL = ");
		query.append(noControl);
		query.append(" AND NOPREAFILIACION= ");
		query.append(noPreAfiliacion);
		query.append(" AND CLAVEPARENTESCO = ");
		query.append(claveParentesco);
				
		Beneficiario beneficiario = null;
		try {
			beneficiario =  mysqlTemplate.queryForObject(query.toString(), new BeneficiarioRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beneficiario;
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

