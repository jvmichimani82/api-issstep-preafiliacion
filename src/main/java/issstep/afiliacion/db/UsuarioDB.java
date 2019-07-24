package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import issstep.afiliacion.model.Usuario;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Descripcion;

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
	
	public int insertar (Derechohabiente derechohabiente, Usuario usuario, long claveParentesco) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO USUARIO "
				+ "(NOCONTROL, CLAVEROL, LOGIN, PASSWORD, TOKEN, FECHAREGISTRO, ESTATUS, NOAFILIACION )"
				+ " VALUES(?,?,?,?,?,?,?,?)");

		System.out.println(query.toString());
		
		if (registraBeneficiario( derechohabiente, claveParentesco ) == 1) {
			try {
				
				return  mysqlTemplate.update(query.toString(), new Object[] { usuario.getNoControl(),
						usuario.getClaveRol(), usuario.getLogin(), usuario.getPasswd(), usuario.getToken(), 
						usuario.getFechaRegistro(), usuario.getEstatus(), usuario.getNoAfiliacion()});
			} catch (Exception e) {
				e.printStackTrace();
			}			
		};		
		return 0;
	}
	
	public int registraBeneficiario(Derechohabiente derechohabiente, long claveParentesco) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO BENEFICIARIO "
				+ "(NOCONTROL, NOPREAFILIACION, CLAVEPARENTESCO, FECHAAFILIACION, SITUACION, CLAVEUSUARIOREGISTRO, FECHAREGISTRO) "
				+ "VALUES(?,?,?,?,?,?,?)");
		
		try {
			
			return  mysqlTemplate.update(query.toString(), new Object[] { derechohabiente.getNoControl(),
					derechohabiente.getNoPreAfiliacion(), claveParentesco, derechohabiente.getFechaPreAfiliacion(),
					1, derechohabiente.getClaveUsuarioRegistro(), derechohabiente.getFechaRegistro() });
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	
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
	
	public long createUsuario( long claveParentesco, Usuario usuario ) {
		return createOrDeleteUsuario( claveParentesco, usuario, 0, "create");
	}
	
	public long deleteUsuario(  long claveParentesco, long claveUsuario ) {
		return createOrDeleteUsuario( claveParentesco, null, claveUsuario,"delete");
	}
	
	public long createOrDeleteUsuario(  long claveParentesco, Usuario usuario, long claveUsuario, String opcion) {
		StringBuilder query = new StringBuilder();
		
		
		if (opcion.equals("create")) 		
			query.append("INSERT INTO USUARIO "
					+ "(CLAVEROL, LOGIN, PASSWORD, TOKEN, FECHAREGISTRO, ESTATUS, NOCONTROL,  NOAFILIACION  )"
					+ " VALUES( "
					+ usuario.getClaveRol() + ", '" + usuario.getLogin() + "', '" + usuario.getPasswd() + "', '"
					+ usuario.getToken() + "', '" + usuario.getFechaRegistro() + "', " + usuario.getEstatus() + ", "
					+ usuario.getNoControl() + ", " + usuario.getNoAfiliacion()
					+ ")");
		
		else 
			query.append("DELETE FROM USUARIO WHERE CLAVEUSUARIO = " + claveUsuario);
		
		System.out.println(query.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			mysqlTemplate.update(
	    	    new PreparedStatementCreator() {
	    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	    	            PreparedStatement pst = con.prepareStatement(query.toString(), new String[] {"claveUsuario"});
	    	            return pst;
	    	        }
	    	    },
	    	    keyHolder);
			
			return (opcion.equals("create")) ? (long) keyHolder.getKey() : (long) 1;		
	    	
		} catch (Exception e) {
			e.printStackTrace();
			return (long) 0;
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

