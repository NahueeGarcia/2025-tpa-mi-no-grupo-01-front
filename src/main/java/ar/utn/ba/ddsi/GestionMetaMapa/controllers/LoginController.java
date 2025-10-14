package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

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
                                HttpServletRequest request, // Pedimos el objeto request
                                Model model) {

        AuthResponseDTO authResponse = loginService.autenticar(username, password);

        if (authResponse != null && authResponse.getAccessToken() != null) {
            // 1. Guardamos el token JWT en la sesión para usarlo en las llamadas a la API
            HttpSession session = request.getSession();
            session.setAttribute("jwt_token", authResponse.getAccessToken());

            // 2. Creamos un objeto de autenticación para Spring Security
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, new ArrayList<>()); // El password es null, los roles vacíos

            // 3. Establecemos este objeto en el contexto de seguridad
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 4. Guardamos el contexto en la sesión HTTP para que persista
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            return "redirect:/";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
}