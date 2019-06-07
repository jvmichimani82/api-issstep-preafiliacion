package issstep.afiliacion.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Usuario;

@Component
public class UsuarioDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	public Usuario getSession(String usuario, String passwd) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT CONSECUTIVO, NOMBRE FROM USUARIO WHERE USUARIO ='");
		query.append(usuario);
		query.append("' AND PASSWORD ='");
		query.append(passwd);
		query.append("'");
		Usuario user = null;
		try {
			user =  mysqlTemplate.queryForObject(query.toString(), new UsuarioRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	
	
}

class UsuarioRowMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Usuario usuario = new Usuario();
 
    	usuario.setId(rs.getLong("CONSECUTIVO"));
        usuario.setNombre(rs.getString("NOMBRE"));
 
        return usuario;
    }
}

