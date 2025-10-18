package ar.utn.ba.ddsi.GestionMetaMapa.providers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.RolesPermisosDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.services.GestionMetaMapaApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Proveedor de autenticación personalizado que delega la validación de credenciales
 * al backend (Servidor de Aplicaciones) a través de la API.
 */
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    @Autowired
    private GestionMetaMapaApiService apiService;

    @Override
    public Authentication authenticate(Authentication authentication) throws
            AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 1. Se llama al servicio central para hacer el POST al backend y obtener el token.
        AuthResponseDTO authResponse = apiService.login(username, password);

        if (authResponse == null) {
            throw new BadCredentialsException("Credenciales inválidas o error en el servicio de autenticación.");
        }

        // --- MANEJO DE TOKEN (Frontend <-> Backend) ---
        // El token JWT es el "pasaporte" que este servidor de frontend usará para
        // identificarse ante el servidor de backend en nombre del usuario.
        // Se guarda en la sesión del servidor para estar disponible en futuras peticiones.
        String accessToken = authResponse.getAccessToken();
        String refreshToken = authResponse.getRefreshToken();

        // --- MANEJO DE COOKIES (Usuario <-> Frontend) ---
        // No manipulamos la cookie directamente. Al obtener la HttpSession, Spring
        // se encarga de crear y enviar la cookie de sesión (JSESSIONID) al navegador.
        // Esta cookie es la que permite al navegador del usuario mantener su sesión
        // con nuestro servidor de frontend.
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(true); // Crea una sesión si no existe

        session.setAttribute("jwt_token", accessToken);
        session.setAttribute("refresh_token", refreshToken);
        session.setAttribute("username", username);

        // 2. Con el token obtenido, se hace una segunda llamada para obtener los roles.
        RolesPermisosDTO rolesDto = apiService.getRolesPermisos(accessToken);
        if (rolesDto == null || rolesDto.getRol() == null) {
            throw new BadCredentialsException("No se pudieron obtener los roles del usuario.");
        }

        // --- MANEJO DE SESIONES CON ROLES ---
        // Se crea la lista de "authorities" que Spring Security entiende.
        // El prefijo "ROLE_" es una convención estándar y necesaria para que las
        // anotaciones como @PreAuthorize("hasRole('ADMIN')") funcionen correctamente.
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +
                rolesDto.getRol()));

        // 3. Se devuelve un objeto Authentication completo y "autenticado".
        // Spring Security lo recibe, lo guarda en el SecurityContext y considera el login exitoso.
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}