package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
 public class AdminController {

    @GetMapping("/panel")
    @PreAuthorize("hasRole('ADMIN')") // ¡La línea clave! Solo los ADMIN pueden acceder.
    public String panel() {
        return "admin/panel"; // Devuelve la plantilla que crearemos a continuación.
    }
}