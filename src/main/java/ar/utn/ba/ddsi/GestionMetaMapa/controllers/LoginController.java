
package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.services.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login() {
        // Simplemente muestra la página de login (login.html)
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        AuthResponseDTO authResponse = loginService.autenticar(username, password);

        if (authResponse != null && authResponse.getAccessToken() != null) {
            // ¡Login exitoso! Guardamos el token en la sesión del usuario.
            session.setAttribute("jwt_token", authResponse.getAccessToken());
            // Redirigimos al usuario a la página de colecciones.
            return "redirect:/metamapa/colecciones";
        } else {
            // Login fallido. Volvemos a mostrar la página de login con un mensaje de error.
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
         }
    }
}