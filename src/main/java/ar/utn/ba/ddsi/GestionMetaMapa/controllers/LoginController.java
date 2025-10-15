package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpServletRequest request,
                                Model model) {

        AuthResponseDTO authResponse = loginService.autenticar(username, password);

        if (authResponse != null && authResponse.getAccessToken() != null) {
            UserRolesPermissionsDTO rolesDto = loginService.obtenerRolesYPermisos(authResponse.getAccessToken());
            if (rolesDto != null) {
                establecerSesionDeSeguridad(request, username, rolesDto.getRol(), authResponse.getAccessToken());
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("error", "Credenciales inválidas o error al obtener roles.");
        return "login";
    }

    @GetMapping("/login/visualizador")
    public String loginComoVisualizador(HttpServletRequest request) {
        AuthResponseDTO authResponse = loginService.autenticarComoVisualizador();

        if (authResponse != null && authResponse.getAccessToken() != null) {
            UserRolesPermissionsDTO rolesDto = loginService.obtenerRolesYPermisos(authResponse.getAccessToken());
            if (rolesDto != null) {
                establecerSesionDeSeguridad(request, "visualizador", rolesDto.getRol(), authResponse.getAccessToken());
                return "redirect:/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }

    private void establecerSesionDeSeguridad(HttpServletRequest request, String username, String rol, String token) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

        HttpSession session = request.getSession();
        session.setAttribute("jwt_token", token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                authorities);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }
}