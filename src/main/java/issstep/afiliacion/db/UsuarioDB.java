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
		query.append("SELECT NOUSUARIO, NOROL, NOCONTROL, NOAFILIACION, LOGIN, TOKEN, PASSWORD, FECHAREGISTRO, FECHAULTIMOACCESO, ACTIVO FROM USUARIO WHERE LOGIN ='");
		query.append(usuario);
		query.append("' AND PASSWORD ='");
		query.append(passwd);
		query.append("'");
		Usuario user = null;
		
		System.out.println(query.toString());
		
		try {
			user =  mysqlTemplate.queryForObject(query.toString(), new UsuarioRowMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public Usuario getUsuarioById(long id) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT NOUSUARIO, PASSWORD, NOROL, LOGIN, TOKEN, FECHAREGISTRO, FECHAULTIMOACCESO, ACTIVO FROM USUARIO WHERE NOUSUARIO = ");
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
				+ "(NOUSUARIO, NOCONTROL, NOAFILIACION, NOROL, LOGIN, PASSWORD, TOKEN, ACTIVO)"
				+ " VALUES(?,?,?,?,?,?,?,?)");

		System.out.println("Insersion ==> "+ query.toString());
	
		
		try {
			return  mysqlTemplate.update(query.toString(), new Object[] { usuario.getNoControl(),
					usuario.getNoControl(), usuario.getNoAfiliacion(),
					usuario.getNoRol(), usuario.getLogin(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getActivo() 
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void actualiza (Usuario usuario) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE USUARIO SET NOROL = ?, PASSWORD = ?, TOKEN = ?, FECHAULTIMOACCESO = ?, ACTIVO = ? WHERE NOUSUARIO = ? ");
			
		System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					usuario.getNoRol(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getFechaUltimoAcceso(),usuario.getActivo(), usuario.getNoControl()
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
 
    	usuario.setNoControl(rs.getLong("NOUSUARIO"));
    	usuario.setNoRol(rs.getLong("NOROL"));
        usuario.setLogin(rs.getString("LOGIN"));
        usuario.setPasswd(rs.getString("PASSWORD"));
        usuario.setToken(rs.getString("TOKEN"));
        usuario.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        usuario.setFechaUltimoAcceso(rs.getTimestamp("FECHAULTIMOACCESO"));
        usuario.setActivo(rs.getInt("ACTIVO"));
 
        return usuario;
    }
}

