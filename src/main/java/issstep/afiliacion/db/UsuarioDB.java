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
		query.append("SELECT USUARIO, ROL, LOGIN, TOKEN, FECHAREGISTRO, ULTIMOREGISTRO, ESTATUS, FROM USUARIO WHERE LOGIN ='");
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
	
	public Usuario getUsuarioById(long id) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT USUARIO, PASSWORD, ROL, LOGIN, TOKEN, FECHAREGISTRO, ULTIMOREGISTRO, ESTATUS FROM USUARIO WHERE USUARIO = ");
		query.append(id);
	
		Usuario user = null;
		try {
			user =  mysqlTemplate.queryForObject(query.toString(), new UsuarioRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public Usuario getUsuarioByToken(String token) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT USUARIO, PASSWORD, ROL, LOGIN, TOKEN, FECHAREGISTRO, ULTIMOREGISTRO, ESTATUS FROM USUARIO WHERE TOKEN = '");
		query.append(token+"'");
	
		Usuario user = null;
		try {
			user =  mysqlTemplate.queryForObject(query.toString(), new UsuarioRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public int insertar (Usuario usuario) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO USUARIO "
				+ "(USUARIO, ROL, LOGIN, PASSWORD, TOKEN, FECHAREGISTRO, ULTIMOREGISTRO, ESTATUS)"
				+ " VALUES(?,?,?,?,?,?,?,?)");

		System.out.println(query.toString());
		
		try {
			return  mysqlTemplate.update(query.toString(), new Object[] { usuario.getId(),
					usuario.getRol(), usuario.getLogin(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getFechaRegistro(),usuario.getUltimaModificacion(),usuario.getEstatus() 
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void actualiza (Usuario usuario) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE USUARIO SET ROL = ?, PASSWORD = ?, TOKEN = ?, ULTIMOREGISTRO = ?, ESTATUS = ? WHERE USUARIO = ? ");
			
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					usuario.getRol(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getUltimaModificacion(),usuario.getEstatus(), usuario.getId()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}

class UsuarioRowMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Usuario usuario = new Usuario();
 
    	usuario.setId(rs.getLong("USUARIO"));
    	usuario.setRol(rs.getLong("ROL"));
        usuario.setLogin(rs.getString("LOGIN"));
        usuario.setPasswd(rs.getString("PASSWORD"));
        usuario.setToken(rs.getString("TOKEN"));
        usuario.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        usuario.setUltimaModificacion(rs.getTimestamp("ULTIMOREGISTRO"));
        usuario.setEstatus(rs.getInt("ESTATUS"));
 
        return usuario;
    }
}

