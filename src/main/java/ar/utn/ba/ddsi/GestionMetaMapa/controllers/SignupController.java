package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.SignupDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.services.GestionMetaMapaApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    private final GestionMetaMapaApiService apiService;

    public SignupController(GestionMetaMapaApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        // Pasamos un objeto vacío para bindear el formulario
        model.addAttribute("usuario", new SignupDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("usuario") SignupDTO registroDTO,
                                @RequestParam String confirmPassword,
                                Model model) {

        // 1. Validación básica de contraseñas
        if (!registroDTO.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "signup";
        }

        try {
            // 2. Llamar al servicio API para registrar
            apiService.registrarUsuario(registroDTO);

            // 3. Si sale bien, redirigir al login con mensaje de éxito
            return "redirect:/login?registrado=true";

        } catch (Exception e) {
            // 4. Si falla (ej: email ya existe), volver al formulario con el error
            model.addAttribute("error", "No se pudo registrar el usuario. Verifique los datos o intente más tarde.");
            // System.out.println(e.getMessage()); // Para debug
            return "signup";
        }
    }
}
