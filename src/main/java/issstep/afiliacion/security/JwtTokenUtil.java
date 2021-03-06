package issstep.afiliacion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import issstep.afiliacion.model.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;
    private static final String CLAIM_KEY_IDROL = "idRol";
    private static final String CLAIM_KEY_ROL = "rol";
    private static final String CLAIM_KEY_IDOFICINA = "idOficina";
    private static final String CLAIM_KEY_NAMEOFICINA = "nombreOficina";
    private static final String CLAIM_KEY_OTT = "ott";
    private static final String CLAIM_KEY_IDPREAFILIA = "idPre";
    private static final String CLAIM_KEY_DEP = "dep";
    private static final String CLAIM_KEY_IDUSER = "idUser";
    private static final String CLAIM_KEY_USER = "user";
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_AUDIENCE = "audience";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);	
          
    public MessageUtils messageUtils = new MessageUtils();

    public String getUsernameFromToken(String token) {
    	 
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    
    public String getRolFromToken(String token) {
        String rol;
        try {
            final Claims claims = getClaimsFromToken(token);
            rol = (String) claims.get(CLAIM_KEY_ROL);
        } catch (Exception e) {
            rol = null;
        }
        return rol;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = getClaimsFromToken(token);
            audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
          claims = Jwts.parser()
                    .setSigningKey(messageUtils.getMessage("secret_jwt_token"))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
        	//logger.error(token, e.printStackTrace());
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate() {
    	Long expiration = Long.parseLong(messageUtils.getMessage("duracion_jwt_token"));
    	Calendar fechaExpira = Calendar.getInstance();
    	// System.out.println(fechaExpira.getTimeInMillis() + expiration * 10000);
    	return new Date(fechaExpira.getTimeInMillis() + expiration * 10000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(UserDetails userDetails, Device device, Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ROL, usuario.getRol());
       // claims.put(CLAIM_KEY_IDUSER, usuario.getNoUsuario() );
       	claims.put(CLAIM_KEY_USERNAME, usuario.getLogin());
       	//claims.put(CLAIM_KEY_ROL, usuario.getRol() != null ?  usuario.getRol().getClave() : "");
       	claims.put(CLAIM_KEY_IDUSER, usuario.getNoControl());
       	claims.put(CLAIM_KEY_IDPREAFILIA, usuario.getNoAfiliacion());
       	
        claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));
        claims.put(CLAIM_KEY_CREATED, new Date());
        
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
    	// System.out.println(generateExpirationDate());
    	
    	  return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, messageUtils.getMessage("secret_jwt_token"))
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return /*!isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && */(!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Usuario user = (Usuario) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                //username.equals(user.getUsername()) &&
                        !isTokenExpired(token)
                        /*&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())*/);
    }
}