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
		query.append("SELECT CLAVEUSUARIO, CLAVEROL, LOGIN, TOKEN, PASSWORD, FECHAREGISTRO, FECHAULTIMOACCESO, ESTATUS, NOCONTROL, NOAFILIACION FROM USUARIO WHERE LOGIN ='");
		query.append(usuario);
		query.append("' AND PASSWORD ='");
		query.append(passwd);
		query.append("'");
		
		System.out.println(query.toString());
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
		query.append("SELECT CLAVEUSUARIO, CLAVEROL, LOGIN, TOKEN, PASSWORD, FECHAREGISTRO, FECHAULTIMOACCESO, ESTATUS, NOCONTROL, NOAFILIACION FROM USUARIO WHERE USUARIO = ");
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
		query.append("SELECT CLAVEUSUARIO, CLAVEROL, LOGIN, TOKEN, PASSWORD, FECHAREGISTRO, FECHAULTIMOACCESO, ESTATUS, NOCONTROL, NOAFILIACION FROM USUARIO WHERE TOKEN = '");
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
				+ "(NOCONTROL, CLAVEROL, LOGIN, PASSWORD, TOKEN, FECHAREGISTRO, ESTATUS, NOAFILIACION )"
				+ " VALUES(?,?,?,?,?,?,?,?)");

		System.out.println(query.toString());
		
		try {
			return  mysqlTemplate.update(query.toString(), new Object[] { usuario.getNoControl(),
					usuario.getClaveRol(), usuario.getLogin(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getFechaRegistro(), usuario.getEstatus(), usuario.getNoAfiliacion()});
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
			  /*mysqlTemplate.update(query.toString(), new Object[] { 
					usuario.getRol(), usuario.getPasswd(), usuario.getToken(), 
					usuario.getUltimaModificacion(),usuario.getEstatus(), usuario.getId()
			});*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}

class UsuarioRowMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Usuario usuario = new Usuario();
 
    	usuario.setClaveUsuario(rs.getLong("CLAVEUSUARIO"));
    	usuario.setClaveRol(rs.getLong("CLAVEROL"));
    	usuario.setLogin(rs.getString("LOGIN"));
    	usuario.setToken(rs.getString("TOKEN"));
    	usuario.setPasswd(rs.getString("PASSWORD"));
    	usuario.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
    	usuario.setFechaUltimoAcceso(rs.getTimestamp("FECHAULTIMOACCESO"));
    	usuario.setEstatus(rs.getInt("ESTATUS")); 	
    	usuario.setNoControl(rs.getLong("NOCONTROL"));
    	usuario.setNoAfiliacion(rs.getLong("NOAFILIACION"));
    	
        return usuario;
    }
}

