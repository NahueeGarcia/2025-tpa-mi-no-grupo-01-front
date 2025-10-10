package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/metamapa";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("titulo", "No encontrado");
        return "404";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("titulo", "Acceso denegado");
        model.addAttribute("mensaje", "No tiene permiso para acceder a este recurso.");
        return "403";
    }
}
