package issstep.afiliacion.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import issstep.afiliacion.model.DocumentosFaltantes;
import issstep.afiliacion.model.InfoDerechohabiente;
import issstep.afiliacion.model.InfoPersona;
import issstep.afiliacion.model.NumerosParaRegistro;
import issstep.afiliacion.model.ResultadoBusqueda;
import issstep.afiliacion.utils.Utils;

@Component
public class DerechohabienteDB {

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;
	
	@Autowired
	@Qualifier("afiliacionJdbcTemplate")
	private JdbcTemplate afiliacionDBTemplate;
	
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public Derechohabiente getPersonaByColumnaStringValor(String columna, String valor) {
		StringBuilder query = new StringBuilder();
		
		query.append( "SELECT DH.* FROM WDERECHOHABIENTE DH WHERE DH.");
		
		query.append(columna);
		query.append("= '");
		query.append(valor);
		query.append("'");
		
		// System.out.println("Consulta ==> " + query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return persona;
	}
	
	//obtenemos el trabajador o derechoabiente de la bd de issstep
	
	public Derechohabiente getTrabajadorIssstepByColumnaStringValor(String columna, String valor) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT T.* FROM TRABAJADOR T "
					+ "WHERE T.");
		
		query.append(columna);
		query.append("= '");
		query.append(valor);
		query.append("'");
		
		// System.out.println("Consulta ==> " + query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  afiliacionDBTemplate.queryForObject(query.toString(), new TrabajadorRowMapper());		
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return persona;
	}
	
	
	public Derechohabiente getPersonaByNombre(Derechohabiente persona, HttpServletResponse response) {
		StringBuilder query = new StringBuilder();
		
		query.append( "SELECT * FROM WDERECHOHABIENTE WHERE PATERNO LIKE '%" + persona.getPaterno() + "%' "
				    + "AND MATERNO LIKE '%" + persona.getMaterno() 
				    + "%' AND NOMBRE LIKE '%" + persona.getNombre()
				    + "%' AND CLAVEESTADO = " + persona.getClaveEstado()
				    + " AND FECHANACIMIENTO = '" + format.format(persona.getFechaNacimiento()) +"'");	
		
		// System.out.println("Consulta ==> " + query.toString());
				
		Derechohabiente personaOld = null;
		try {
			personaOld =  mysqlTemplate.queryForObject(query.toString().toUpperCase(), new PersonaRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			response.setStatus(429);
			return null;
		}
		return personaOld;
	}
	
	public Derechohabiente getPersonaById(long noControl) {
		StringBuilder query = new StringBuilder();
		
		query.append( "SELECT * FROM WDERECHOHABIENTE WHERE NOCONTROL ="); 
		query.append(noControl);
		query.append(" AND NOPREAFILIACION = ");
		query.append(noControl);
				
		// System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	public Derechohabiente getPersonaByNoControlNoPreafiliacion(InfoPersona infoPersona) {
		StringBuilder query = new StringBuilder();
			if (infoPersona.getClaveParentesco() == 0) 
				query.append( "SELECT * FROM WDERECHOHABIENTE \n"
						+ "		WHERE NOCONTROL = " + infoPersona.getNoControl() +"\n"
						+ " 		  AND NOPREAFILIACION = " + infoPersona.getNoPreAfiliacion()); 
			else 
				query.append( "SELECT BENEF.NOCONTROL, BENEF.NOPREAFILIACION, BENEF.NOMBRE, BENEF.PATERNO, BENEF.MATERNO, \n"
							+ "	      TIT.EMAIL, BENEF.FECHANACIMIENTO, BENEF.SEXO, BENEF.CURP, BENEF.RFC, TIT.DIRECCION, \n"
							+ "       TIT.CODIGOPOSTAL, TIT.TELEFONOCASA, TIT.TELEFONOCELULAR, BENEF.FECHAAFILIACION  AS FECHAPREAFILIACION, \n"
							+ "       BENEF.SITUACION, BENEF.CLAVEUSUARIOREGISTRO, BENEF.FECHAREGISTRO, \n"
							+ "       BENEF.CLAVEUSUARIOMODIFICACION, BENEF.FECHAMODIFICACION, BENEF.CLAVEESTADOCIVIL, \n"
							+ "       TIT.CLAVECOLONIA, TIT.CLAVECLINICASERVICIO, TIT.CLAVEESTADO, TIT.CLAVEMUNICIPIO, \n"
							+ "       TIT.CLAVELOCALIDAD, BENEF.ESTATUS \n"
							+ "	FROM \n"
							+ "		 (SELECT B.NOCONTROLTITULAR, B.NOCONTROL, B.NOPREAFILIACION, DH.NOMBRE, DH.PATERNO, DH.MATERNO, \n"
							+ "				 DH.FECHANACIMIENTO, DH.SEXO, DH.CURP, DH.RFC, B.FECHAAFILIACION, \n"
							+ "                 B.SITUACION, B.CLAVEUSUARIOREGISTRO, B.FECHAREGISTRO, \n"
							+ "                 B.CLAVEUSUARIOMODIFICACION, B.FECHAMODIFICACION, DH.CLAVEESTADOCIVIL, \n"
							+ "                 DH.ESTATUS \n"
							+ "                 FROM \n"
							+ "					(SELECT * \n"
							+ "						FROM WBENEFICIARIO \n"
							+ "						WHERE NOCONTROLTITULAR =" + infoPersona.getNoControlTitular() + "\n"
							+ "							  AND NOCONTROL = " + infoPersona.getNoControl() + "\n"
							+ "							  AND NOPREAFILIACION = " + infoPersona.getNoPreAfiliacion()  + ") B, \n"
							+ "					(SELECT * \n"
							+ "						FROM  WDERECHOHABIENTE \n"
							+ "						WHERE NOCONTROL = " + infoPersona.getNoControl() + "\n"
							+ "							  AND NOPREAFILIACION = " + infoPersona.getNoPreAfiliacion() + ") DH) BENEF, \n"
							+ "		 (SELECT *, noControl AS NOCONTROLTITULAR \n"
							+ "				FROM WDERECHOHABIENTE \n"
							+ "                WHERE NOCONTROL = " + infoPersona.getNoControlTitular() + "\n"
							+ "						 AND NOPREAFILIACION = " + infoPersona.getNoControlTitular() + ") TIT \n"
							+ "	WHERE BENEF.NOCONTROLTITULAR = TIT.NOCONTROLTITULAR");

				
		// System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  mysqlTemplate.queryForObject(query.toString(), new PersonaRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	public Derechohabiente getAfiliadoByNoControlNoPreafiliacion(long noControl, long noAafiliacion, long claveParentesco) {
		boolean esTitular = claveParentesco == 0;
		
		StringBuilder query = new StringBuilder();
		
		if (esTitular)
			query.append( "SELECT *, 0 AS CLAVEPARENTESCO FROM TRABAJADOR WHERE NOCONTROL = "); 
		else 
			query.append( "SELECT * FROM  BENEFICIARIO  WHERE NOAFILIACION = ");		
		query.append(noControl);
		
		if (esTitular)
			query.append(" AND NOAFILIACION = ");	
		else
			query.append(" AND NOBENEFICIARIO = ");
		query.append(noAafiliacion);
				
		System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			if (esTitular)
				persona =  afiliacionDBTemplate.queryForObject(query.toString(), new TrabajadorRowMapper());
			else 
				persona =  afiliacionDBTemplate.queryForObject(query.toString(), new BeneficiarioRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
	
	public Derechohabiente getPersonaByNoControlNoAfiliacionIssstep(long noControl, long noAfiliacion) {
		StringBuilder query = new StringBuilder();
			query.append( "SELECT * FROM TRABAJADOR WHERE NOCONTROL ="); 
			query.append(noControl);
			query.append(" AND NOAFILIACION =");
			query.append(noAfiliacion);
				
		// System.out.println(query.toString());
		
		Derechohabiente persona = null;
		try {
			persona =  afiliacionDBTemplate.queryForObject(query.toString(), new TrabajadorRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return persona;
	}
		
	public void actualiza (Derechohabiente persona) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE WDERECHOHABIENTE SET NOMBRE= ?, PATERNO= ?, MATERNO = ?, EMAIL= ?, FECHAREGISTRO= ?, SITUACION= ?, CLAVEUSUARIOREGISTRO= ? WHERE NOCONTROL = ? ");
					
		// System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString(), new Object[] { 
					  persona.getNombre(), persona.getPaterno(), persona.getMaterno(), 
					  persona.getEmail(), persona.getFechaRegistro(), persona.getSituacion(), 
					  persona.getClaveUsuarioRegistro(),persona.getNoControl()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public long actualizaDatos(boolean esAdmin, Derechohabiente datosDerechohabiente) {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE WDERECHOHABIENTE SET ");
		
		if (esAdmin) 
			query.append( " EMAIL = '" + datosDerechohabiente.getEmail()
						+ "', RFC = '" + datosDerechohabiente.getRfc()
						+ "', CODIGOPOSTAL = '" + datosDerechohabiente.getCodigoPostal()
						+ "', CLAVEESTADOCIVIL = " + datosDerechohabiente.getClaveEstadoCivil()
						+ ", CLAVECOLONIA = " + datosDerechohabiente.getClaveColonia()
						+ ", CLAVELOCALIDAD = " + datosDerechohabiente.getClaveLocalidad()
						+ ", CLAVEMUNICIPIO = " + datosDerechohabiente.getClaveMunicipio()
						+ ", CLAVEESTADO = " + datosDerechohabiente.getClaveEstado()
						+ ", ");
		
		query.append(" DIRECCION = '"
					+ datosDerechohabiente.getDireccion()
					+ "', TELEFONOCASA = '"
					+ datosDerechohabiente.getTelefonoCasa() 
					+ "', TELEFONOCELULAR = '"
					+ datosDerechohabiente.getTelefonoCelular());
		
		if (esAdmin)
			query.append("', ESTATUS = 6");
		else 
			query.append("', ESTATUS = 7");
		
		query.append( " WHERE NOCONTROL = "
					+ datosDerechohabiente.getNoControl() 
					+ " AND NOPREAFILIACION = " 
					+ datosDerechohabiente.getNoPreAfiliacion());
					
		// System.out.println(query.toString());
		
		try {
			  mysqlTemplate.update(query.toString());
			  return datosDerechohabiente.getNoControl();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}		
	}
	
	public long createDerechohabiente( Derechohabiente derechohabiente, boolean esAdmin, int estatus ) {
		return createOrDeleteDerechohabiente( derechohabiente, 0, esAdmin, estatus, "create");
	}
	
	public long deleteDerechohabiente(  long noControl ) {
		return createOrDeleteDerechohabiente( null, noControl, false, 1, "delete");
	}
	
	public long createOrDeleteDerechohabiente(  Derechohabiente derechohabiente, long noControl, boolean esAdmin, int estatus, String opcion) {
		StringBuilder queryTrabajador = new StringBuilder();
		StringBuilder queryDerechohabiente = new StringBuilder();
				
		if (opcion.equals("create")) {				
			queryTrabajador.append("INSERT INTO TRABAJADOR (NOCONTROL, NOAFILIACION, NOMBRE, PATERNO, "
								 + "MATERNO, FECHANACIMIENTO, SEXO, CURP, RFC, DOMICILIO, "
								 + "CODIGOPOSTAL, TELEFONO, FECHAAFILIACION, "
								 + "SITUACION, CLAVEUSUARIOCAPTURA,");
			
			queryDerechohabiente.append("INSERT INTO WDERECHOHABIENTE (NOCONTROL, NOPREAFILIACION, NOMBRE, PATERNO, "
									  + "MATERNO, EMAIL, FECHANACIMIENTO, SEXO, CURP, RFC, DIRECCION, "
									  + "CODIGOPOSTAL, TELEFONOCASA, TELEFONOCELULAR, "
									  + "FECHAPREAFILIACION, SITUACION, CLAVEUSUARIOREGISTRO,");
			
			String queryComun1 =  " FECHAREGISTRO, CLAVEUSUARIOMODIFICACION, "
								+ "CLAVEESTADOCIVIL, CLAVECOLONIA, "
								+ "CLAVECLINICASERVICIO, CLAVELOCALIDAD, CLAVEMUNICIPIO, CLAVEESTADO";
			
			queryTrabajador.append( queryComun1 );
			queryDerechohabiente.append( queryComun1 + ", ESTATUS");
			
			String queryComun2 = ") VALUES ("
				+ derechohabiente.getNoControl() + ", " 
				+ derechohabiente.getNoPreAfiliacion()  
				+ ", '" + derechohabiente.getNombre().trim() + "'" + ", '" 
				+ derechohabiente.getPaterno().toString() + "'"
				+ ", '" + derechohabiente.getMaterno().toString() + "'";
			
			queryTrabajador.append( queryComun2 );
			queryDerechohabiente.append( queryComun2 + ", '" + derechohabiente.getEmail() + "'" );
			
			String queryComun3 = ", '" + Utils.getFechaFromDate(derechohabiente.getFechaNacimiento())+ "', '"  + derechohabiente.getSexo()
				+ "', '" + derechohabiente.getCurp() + "'" + ", '" + derechohabiente.getRfc() + "'"
				+ ", '" + derechohabiente.getDireccion() + "'" + ", '" + derechohabiente.getCodigoPostal() + "'"
				+ ", '" + derechohabiente.getTelefonoCasa() + "',";
			
			queryTrabajador.append( queryComun3 );
			queryDerechohabiente.append( queryComun3 + "'" + derechohabiente.getTelefonoCelular() + "',");
			
			String queryComun4 = " '" + Utils.getFechaFromTimeStamp(derechohabiente.getFechaPreAfiliacion()) + "', "  + derechohabiente.getSituacion() + ", " 
				+ derechohabiente.getClaveUsuarioRegistro() + ", '"  + Utils.getFechaFromTimeStamp(derechohabiente.getFechaRegistro()) + "', " 
				+ derechohabiente.getClaveUsuarioModificacion() + ", "  
				+ (derechohabiente.getClaveEstadoCivil() > 0 ? derechohabiente.getClaveEstadoCivil() : null) + ", "  
				+ derechohabiente.getClaveColonia() + ", " 
				+ derechohabiente.getClaveClinicaServicio() + ", "  + derechohabiente.getClaveLocalidad() + ", " 
				+ derechohabiente.getClaveMunicipio() + ", "  + derechohabiente.getClaveEstado();
				
			queryTrabajador.append( queryComun4 + ")" );
			queryDerechohabiente.append( queryComun4 + ", "+ estatus +  ")" );
			
		}
		else 
			queryDerechohabiente.append("DELETE FROM WDERECHOHABIENTE WHERE noControl = " + noControl);
		
		/* System.out.println("createOrDeleteDerechohabiente (Trabajador) " + queryTrabajador.toString()); 
		System.out.println("createOrDeleteDerechohabiente (Derechohabiente) " + queryDerechohabiente.toString()); */
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			/* if (derechohabiente.getClaveParentesco() == 0)
				afiliacionDBTemplate.update(
		    	    new PreparedStatementCreator() {
		    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    	            PreparedStatement pst = con.prepareStatement(queryTrabajador.toString(), new String[] {"noControl"});
		    	            return pst;
		    	        }
		    	    },
		    	    keyHolder); */
			// System.out.println("Consulta ==> "+queryDerechohabiente.toString());
			mysqlTemplate.update(
		    	    new PreparedStatementCreator() {
		    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    	            PreparedStatement pst = con.prepareStatement(queryDerechohabiente.toString(), new String[] {"noControl"});
		    	            return pst;
		    	        }
		    	    },
		    	    keyHolder);
			
			return (opcion.equals("create")) ? (long) derechohabiente.getNoControl() : (long) 1;		
	    	
		} 
		catch (DataIntegrityViolationException e) {
			return (long) -1;
	    }
		catch (Exception e) {
			e.printStackTrace();
			return (long) 0;
		}
		
	}
	
	public List<InfoDerechohabiente> getDerechohabientesPorEstatusDeValidacion( int estatusValidacion) {
		
		StringBuilder query = new StringBuilder();
		query.append( "SELECT D.NOCONTROL, D.NOPREAFILIACION, D.NOMBRE, D.PATERNO, D.MATERNO, D.CURP, D.CLAVEUSUARIOREGISTRO "
					+ "FROM WDERECHOHABIENTE D, "
					+ "(SELECT NOCONTROL FROM WDOCUMENTO WHERE ESVALIDO = " + estatusValidacion 
					+ " GROUP BY NOCONTROL) DOC" 
					+ " WHERE D.NOCONTROL = DOC.NOCONTROL AND D.NOPREAFILIACION = DOC.NOCONTROL");
		
		// System.out.println(query.toString());
		
		List<InfoDerechohabiente> lista = null;
		
		try {
			lista = mysqlTemplate.query(query.toString(), new ListaPersonaRowMapper());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<Derechohabiente> getBeneficiariosByDerechohabiente(boolean incluirTitular, long noControl) {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT DH.*, BE.NOBENEFICIARIO, BE.CLAVEPARENTESCO \n"
					+ "	FROM WDERECHOHABIENTE DH, \n"
					+ "     ( SELECT NOCONTROL, NOPREAFILIACION, NOBENEFICIARIO, CLAVEPARENTESCO \n"
					+ "				FROM WBENEFICIARIO"
					+ "				WHERE NOCONTROLTITULAR = " + noControl + "\n"); 
				
		if (!incluirTitular)
			query.append("				AND NOPREAFILIACION != " + noControl + "\n");
				
		query.append(") BE \n"
					+ "	WHERE DH.NOCONTROL = BE.NOCONTROL \n"
					+ " 	  AND DH.NOPREAFILIACION = BE.NOPREAFILIACION ");
		

		// System.out.println("Parentescos ==> " + query.toString());
		List<Derechohabiente> beneficiarios = null;
		try {
			beneficiarios =  mysqlTemplate.query(query.toString(), new DerechohabienteRowMapper());
			if (beneficiarios.isEmpty())
				return null;
			return beneficiarios;
		} 
		catch (EmptyResultDataAccessException e) {
			// System.out.println("No hay daytos");
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<DocumentosFaltantes> getDocumentacionByDerechohabiente(boolean incluirTitular, long noControl) {
		String ambiente = Utils.loadPropertie("ambiente");
		StringBuilder query = new StringBuilder();
		/* query.append( "SELECT \n"
					+ "	NOCONTROL, NOPREAFILIACION, ESVALIDO, COUNT(ESVALIDO) AS NUMDOCS \n"
					+ "    FROM \n"
					+ "	  (SELECT \n"); */
		query.append( "SELECT \n");
		if (ambiente.equals("3"))
			query.append( "        DOCXDH.*, ISNULL(D.ESVALIDO,3) AS ESTATUS \n");
		else 
			query.append( "        DOCXDH.*, IF(ISNULL(D.ESVALIDO), 3, D.ESVALIDO) AS ESTATUS \n");
		query.append( "    FROM \n"
					+ "        (SELECT B.NOCONTROL, B.NOPREAFILIACION, B.CLAVEPARENTESCO, DH.FECHANACIMIENTO, TA.CLAVETIPOARCHIVO, TA.ESOBLIGATORIO \n"
					+ "        FROM WDERECHOHABIENTE DH, WBENEFICIARIO B, WKPARENTESCOTIPOARCHIVO TA \n"
					+ "        WHERE DH.NOCONTROL = B.NOCONTROL "
					+ "				 AND DH.NOPREAFILIACION = B.NOPREAFILIACION"
					+ "				 AND B.CLAVEPARENTESCO = TA.CLAVEPARENTESCO \n"
					+ "			     AND (TA.ESOBLIGATORIO = 1 OR (B.CLAVEPARENTESCO IN (6,7) AND TA.CLAVETIPOARCHIVO = 5)) \n"					
					+ "              AND B.NOCONTROL = ");
		query.append(noControl);
		
		if (!incluirTitular)
			query.append("           AND B.NOPREAFILIACION != " + noControl);
		
		query.append( " ) DOCXDH \n"
					+ "        LEFT JOIN WDOCUMENTO D \n"
					+ "		     ON DOCXDH.NOPREAFILIACION = D.NOPREAFILIACION \n"
					+ "				AND DOCXDH.CLAVEPARENTESCO = D.CLAVEPARENTESCO \n"
					+ "				AND DOCXDH.CLAVETIPOARCHIVO = D.CLAVETIPOARCHIVO");
			/*		+ ") FALTANTES \n"
					+ "        WHERE ESOBLIGATORIO = 1 \n"
					+ "		   GROUP BY NOCONTROL, NOPREAFILIACION, ESVALIDO \n"); */

		// System.out.println("DocumentosFaltantes ==> " + query.toString());
		
		List<DocumentosFaltantes> listDocumentosFaltantes = null;
		try {
			listDocumentosFaltantes =  mysqlTemplate.query(query.toString(), new DocumentosFaltantesRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listDocumentosFaltantes;
	}
	
	public List<Derechohabiente> getBeneficiariosByTrabajadorIssstep(long noControl) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT B.*"
					+ "	FROM BENEFICIARIO B"
					+ "	WHERE B.NOAFILIACION = " + noControl);	
		
		// System.out.println(query.toString());
		List<Derechohabiente> beneficiarios = null;
		try {
			beneficiarios =  afiliacionDBTemplate.query(query.toString(), new BeneficiarioRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return beneficiarios;
	}
	
	public List<ResultadoBusqueda> getInformacionPreAfiliaconByCampo(String campo , String dato, boolean esValorNumerico) {
		dato = dato.toUpperCase();
		StringBuilder query = new StringBuilder();
		
			query.append( "SELECT DH.NOMBRE, DH.PATERNO, DH.MATERNO, DH.NOCONTROL, "
						+ "DH.NOPREAFILIACION, 0 AS NOAFILIACION, BE.NOBENEFICIARIO, "
						+ "DH.CURP, BE.CLAVEPARENTESCO, DH.SEXO "
						+ "FROM WDERECHOHABIENTE DH, "
						+ "     WBENEFICIARIO BE "
						+ "WHERE DH.NOCONTROL = BE.NOCONTROL AND DH.NOPREAFILIACION = BE.NOPREAFILIACION AND ");
		
		if (esValorNumerico)
				query.append( "DH.NOPREAFILIACION = " + dato + "");
		else
			if (campo.equals("NOMBRE"))				
				query.append( "(DH.NOMBRE LIKE '%" + dato + "%' OR DH.PATERNO LIKE '%" + dato + "%' OR DH.MATERNO LIKE '%" + dato + "%')");
			else
				query.append(  "DH.CURP LIKE '%" + dato + "%'");
		
		// System.out.println("Consulta de busqueda (PreAfiliacion) ==> " + query.toString());
		
		List<ResultadoBusqueda> resultadoBusqueda = null;
		
		try {
			resultadoBusqueda =  mysqlTemplate.query(query.toString(), new ResultadoBusquedaRowMapper());		
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return resultadoBusqueda;
	}
	
	
	public List<ResultadoBusqueda> getInformacionAfiliaconByCampo(String campo , String dato, 
																  boolean esValorNumerico, boolean incluirBeneficario) {
		dato = dato.toUpperCase();
		StringBuilder query = new StringBuilder();
		String queryComplemento = "";
		
			query.append( "SELECT T.NOMBRE, T.MATERNO, T.PATERNO, T.NOCONTROL, T.NOAFILIACION, "
						+ "0 AS NOPREAFILIACION, T.NOAFILIACION AS NOBENEFICIARIO, T.CURP, "
						+ "0 AS CLAVEPARENTESCO, T.SEXO " 
						+ "FROM TRABAJADOR T WHERE ");
			queryComplemento = " UNION " + 
							  "SELECT B.NOMBRE, B.MATERNO, B.PATERNO, B.NOAFILIACION AS NOCONTROL, "
							+ "B.NOBENEFICIARIO, 0 AS NOPREAFILIACION, "
							+ "B.NOBENEFICIARIO, B.CURP, B.CLAVEPARENTESCO, B.SEXO " 
							+ "	FROM BENEFICIARIO B WHERE ";
			
		if (esValorNumerico) {
				query.append( "T.NOAFILIACION = " + dato);
				if (incluirBeneficario) {
					query.append( queryComplemento );
					query.append( "B.NOBENEFICIARIO = " + dato);
				}
			}
		else
			if (campo.equals("NOMBRE")) {				
				query.append( "(T.NOMBRE LIKE '%" + campo + "%' OR T.PATERNO LIKE '%" + dato + "%' OR T.MATERNO LIKE '%" + dato + "%')");
				if (incluirBeneficario) {
					query.append( queryComplemento );
					query.append( "(B.NOMBRE LIKE '%" + campo + "%' OR B.PATERNO LIKE '%" + dato + "%' OR B.MATERNO LIKE '%" + dato + "%')"); 
				}
			}
			else {
				query.append( "T.CURP LIKE '%" + dato + "%'");
				if (incluirBeneficario) {
					query.append( queryComplemento );
					query.append( "B.CURP LIKE '%" + dato + "%'");
				}
			}
		
		System.out.println("Consulta de busqueda (Afiliacion) ==> " + query.toString());
		
		List<ResultadoBusqueda> resultadoBusqueda = null;
		
		try {
			resultadoBusqueda =  afiliacionDBTemplate.query(query.toString(), new ResultadoBusquedaRowMapper());
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return resultadoBusqueda;
	}
	
	public int existeBeneficiarioRegistrado(Beneficiario beneficiario) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT * FROM WBENEFICIARIO \n"
					+ "		WHERE NOCONTROLTITULAR = " + beneficiario.getNoControlTitular() + "\n"
					+ "			  AND NOCONTROL = " + beneficiario.getNoControl() + "\n");
		
		long claveParentesco = beneficiario.getClaveParentesco();
		if (claveParentesco == 3 || claveParentesco == 4 || claveParentesco == 5 || claveParentesco == 9)
			query.append(" AND CLAVEPARENTESCO IN (3, 4, 5, 9)");
		else 
			query.append(" AND CLAVEPARENTESCO = " + claveParentesco);
		
		List<Derechohabiente> beneficiarios = null;
		try {
			beneficiarios =  mysqlTemplate.query(query.toString(), new DerechohabienteRowMapper());
			return beneficiarios.isEmpty() ? 0 : 1;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return 2;
		}
	}
	
	public boolean existeBeneficiarioRegistradoById(Beneficiario beneficiario) {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT * FROM WBENEFICIARIO "
					+ "	WHERE NOCONTROLTITULAR = " + beneficiario.getNoControlTitular()
					+ "		AND NOCONTROL = " + beneficiario.getNoControl() 
					+ " 	AND NOPREAFILIACION = " + beneficiario.getNoPreAfiliacion() + "");
		
		// System.out.println("existeBeneficiarioRegistradoById" + query.toString());
		List<Beneficiario> listBeneficiario = null;
		try {
			listBeneficiario =  mysqlTemplate.query(query.toString(), new BeneficiarioRegRowMapper());
			return !listBeneficiario.isEmpty();
		} 
		catch (EmptyResultDataAccessException e) {
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public NumerosParaRegistro getNextNumerosRegistro(long claveParentesco, long noControl) {
		StringBuilder query = new StringBuilder();
		
		/* if (claveParentesco == 0)
			query.append( "SELECT NOCONTROL, NOCONTROL AS NOAFILIACION "
						+ "FROM (SELECT MAX(NOCONTROL) + 100 AS NOCONTROL FROM TRABAJADOR) NUMEROS ;");
		else  */
		query.append("SELECT NOCONTROLTITULAR, NOCONTROL, MAX(NOPREAFILIACION) + 1 AS NOPREAFILIACION "
				   + "	FROM WBENEFICIARIO WHERE NOCONTROLTITULAR = " + noControl
				   + "	GROUP BY NOCONTROLTITULAR, NOCONTROL");

		NumerosParaRegistro numerosParaRegistro  = null;
		try {
			/* if (claveParentesco == 0)
				numerosParaRegistro = afiliacionDBTemplate.queryForObject(query.toString(), new NumerosParaRegistroRowMapper());
			else */
			numerosParaRegistro = mysqlTemplate.queryForObject(query.toString(), new NumerosParaRegistroRowMapper());
			
			return numerosParaRegistro;
		} 
		catch (EmptyResultDataAccessException e) {
			numerosParaRegistro.setNoControl(noControl);
			numerosParaRegistro.setNoPreAfiliacion(noControl + 1);
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}			
	}
	
	/* public Colonia getColonia(String codigoPostal, long claveColonia) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT * FROM KCOLONIA WHERE CODIGOPOSTAL = " + codigoPostal + " AND CLAVECOLONIA =  " + claveColonia);
		
		// System.out.println("getColonia  " + query.toString());
		Colonia colonia  = null;
		
		try {
			colonia = afiliacionDBTemplate.queryForObject(query.toString(), new ColoniaRowMapper());			
			return colonia;
		} 
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}			
	} */
}



class NumerosParaRegistroRowMapper implements RowMapper<NumerosParaRegistro> {
	 @Override
	 public NumerosParaRegistro mapRow(ResultSet rs, int rowNum) throws SQLException {
		 NumerosParaRegistro numerosParaRegistro = new NumerosParaRegistro();
		 
		 numerosParaRegistro.setNoControl(rs.getLong("NOCONTROL"));
		 numerosParaRegistro.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
		 
		return numerosParaRegistro;
	 }
}

class PersonaRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        // persona.setEstado(rs.getString("ENTIDAD"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        // persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        // persona.setLocalidad(rs.getString("LOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        // persona.setColonia(rs.getString("COLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        // persona.setClinicaServicio(rs.getString("CLINICA"));        
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        // persona.setEstadoCivil(rs.getString("ESTADOCIVIL"));
        persona.setEstatus(rs.getInt("ESTATUS"));
        
        return persona;
    }   
}

class PersonaConParentescoRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        // persona.setEstado(rs.getString("ENTIDAD"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        // persona.setMunicipio(rs.getString("MUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        // persona.setLocalidad(rs.getString("LOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        // persona.setColonia(rs.getString("COLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        // persona.setClinicaServicio(rs.getString("CLINICA"));        
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        // persona.setEstadoCivil(rs.getString("ESTADOCIVIL"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        // persona.setParentesco(rs.getString("PARENTESCO"));
        
        return persona;
    } 
    
    
}

class TrabajadorRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DOMICILIO"));
        persona.setCodigoPostal(rs.getString("CODIGOPOSTAL"));
        persona.setTelefonoCasa(rs.getString("TELEFONO"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOCAPTURA"));
        persona.setFechaRegistro(new Timestamp(new Date().getTime()));
        //persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        persona.setEstatus(4);
        
        
        return persona;
    }    
}

class BeneficiarioRegRowMapper implements RowMapper<Beneficiario> {
    @Override
    public Beneficiario mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Beneficiario beneficiario = new Beneficiario();
 
    	beneficiario.setNoControl(rs.getLong("NOCONTROL"));
    	beneficiario.setNoBeneficiario(rs.getLong("NOBENEFICIARIO"));
    	beneficiario.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
    	beneficiario.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
    	beneficiario.setFechaAfiliacion(rs.getTimestamp("FECHAAFILIACION"));
    	beneficiario.setSituacion(rs.getLong("SITUACION"));
    	beneficiario.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
    	beneficiario.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
    	
    	return beneficiario;
    } 
}

class BeneficiarioRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoControl(rs.getLong("NOAFILIACION"));
    	persona.setNoPreAfiliacion(rs.getLong("NOBENEFICIARIO"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DOMICILIO"));
        persona.setTelefonoCasa(rs.getString("TELEFONO"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACIONB"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOCAPTURA"));
        persona.setFechaRegistro(new Timestamp(new Date().getTime()));
        //persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        
        	   
        
        return persona;
    }
  
}

class DerechohabienteRowMapper implements RowMapper<Derechohabiente> {
    @Override
    public Derechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Derechohabiente persona = new Derechohabiente();
 
    	persona.setNoBeneficiario(rs.getLong("NOBENEFICIARIO"));
    	persona.setNoControl(rs.getLong("NOCONTROL"));
    	persona.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setPaterno(rs.getString("PATERNO"));
        persona.setMaterno(rs.getString("MATERNO"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
        persona.setSexo(rs.getString("SEXO"));
        persona.setCurp(rs.getString("CURP"));
        persona.setRfc(rs.getString("RFC"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setTelefonoCasa(rs.getString("TELEFONOCASA"));
        persona.setTelefonoCelular(rs.getString("TELEFONOCELULAR"));
        persona.setFechaPreAfiliacion(rs.getTimestamp("FECHAPREAFILIACION"));
        persona.setSituacion(rs.getInt("SITUACION"));
        persona.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
        persona.setFechaRegistro(rs.getTimestamp("FECHAREGISTRO"));
        persona.setClaveUsuarioModificacion(rs.getLong("CLAVEUSUARIOMODIFICACION"));
        persona.setFechaModificacion(rs.getTimestamp("FECHAMODIFICACION"));
        persona.setClaveEstado(rs.getLong("CLAVEESTADO"));
        persona.setClaveMunicipio(rs.getLong("CLAVEMUNICIPIO"));
        persona.setClaveLocalidad(rs.getLong("CLAVELOCALIDAD"));
        persona.setClaveColonia(rs.getLong("CLAVECOLONIA"));
        persona.setClaveClinicaServicio(rs.getLong("CLAVECLINICASERVICIO"));
        persona.setClaveEstadoCivil(rs.getLong("CLAVEESTADOCIVIL"));
        persona.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        persona.setEstatus(rs.getInt("ESTATUS"));
        
        return persona;
    }   
}

class ResultadoBusquedaRowMapper implements RowMapper<ResultadoBusqueda> {
    @Override
    public ResultadoBusqueda mapRow(ResultSet rs, int rowNum) throws SQLException {
    	ResultadoBusqueda result = new ResultadoBusqueda();
 
    	result.setNoBeneficiario(rs.getLong("NOBENEFICIARIO"));
    	result.setNoControl(rs.getLong("NOCONTROL"));
    	result.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
    	result.setNoAfiliacion(rs.getLong("NOAFILIACION"));
    	result.setNombre(rs.getString("NOMBRE"));
    	result.setPaterno(rs.getString("PATERNO"));
    	result.setMaterno(rs.getString("MATERNO"));
    	result.setSexo(rs.getString("SEXO"));
    	result.setCurp(rs.getString("CURP"));
    	result.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
        
        return result;
    }
}


class ListaPersonaRowMapper implements RowMapper<InfoDerechohabiente> {
    @Override
    public InfoDerechohabiente mapRow(ResultSet rs, int rowNum) throws SQLException {
    	InfoDerechohabiente infoDerechohabiente = new InfoDerechohabiente();
 
    	infoDerechohabiente.setNoControl(rs.getLong("NOCONTROL"));
    	infoDerechohabiente.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
    	infoDerechohabiente.setNombre(rs.getString("NOMBRE"));
    	infoDerechohabiente.setPaterno(rs.getString("PATERNO"));
    	infoDerechohabiente.setMaterno(rs.getString("MATERNO"));
        infoDerechohabiente.setCurp(rs.getString("CURP"));
        infoDerechohabiente.setClaveUsuarioRegistro(rs.getLong("CLAVEUSUARIOREGISTRO"));
           
        return infoDerechohabiente;
    }
}

class DocumentosFaltantesRowMapper implements RowMapper<DocumentosFaltantes> {
    @Override
    public DocumentosFaltantes mapRow(ResultSet rs, int rowNum) throws SQLException {
    	DocumentosFaltantes documentosFaltantes = new DocumentosFaltantes();
 
    	documentosFaltantes.setNoControl(rs.getLong("NOCONTROL"));
    	documentosFaltantes.setNoPreAfiliacion(rs.getLong("NOPREAFILIACION"));
    	documentosFaltantes.setClaveParentesco(rs.getLong("CLAVEPARENTESCO"));
    	documentosFaltantes.setClaveTipoArchivo(rs.getLong("CLAVETIPOARCHIVO"));
    	documentosFaltantes.setEsObligatorio(rs.getInt("ESOBLIGATORIO"));   	
    	documentosFaltantes.setEstatus(rs.getInt("ESTATUS"));
    	documentosFaltantes.setFechaNacimiento(rs.getDate("FECHANACIMIENTO"));
    	         
        return documentosFaltantes;
    }
}
