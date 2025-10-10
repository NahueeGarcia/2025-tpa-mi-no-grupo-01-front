package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.GestionMetaMapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.providers.CustomAuthProvider;
import ar.utn.ba.ddsi.GestionMetaMapa.services.MetaMapaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/metamapa")
@RequiredArgsConstructor
public class MetaMapaController {
    private static final Logger log = LoggerFactory.getLogger(MetaMapaController.class);
    private final MetaMapaService metamapaService;

    @GetMapping("/hechos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VISUALIZADOR', 'CONTRIBUYENTE')")
    public String listarHechos(Model model, Authentication authentication) {
        List<HechoDTO> hechos = metamapaService.obtenerTodosLosHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Listado de hechos");
        model.addAttribute("totalDeHechos", hechos.size());
        model.addAttribute("usuario", authentication.getName());
        return "hechos/lista";
    }

    @GetMapping("/colecciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'VISUALIZADOR', 'CONTRIBUYENTE')")
    public String listarColecciones(Model model, Authentication authentication) {
        List<ColeccionDTO> colecciones = metamapaService.obtenerTodasLasColecciones();
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("titulo", "Listado de colecciones");
        model.addAttribute("totalDeColecciones", colecciones.size());
        model.addAttribute("usuario", authentication.getName());
        return "colecciones/lista";
    }

    @GetMapping("/colecciones/{id}/hechos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VISUALIZADOR', 'CONTRIBUYENTE')")
    public String listarHechosPorColeccion(@PathVariable("id") Long id, @RequestParam(name = "navegacion", defaultValue = "CURADA") String navegacion, Model model, Authentication authentication) {
        List<HechoDTO> hechos = metamapaService.obtenerHechosPorColeccion(id, navegacion);
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Listado de hechos por colección");
        model.addAttribute("totalDeHechos", hechos.size());
        model.addAttribute("usuario", authentication.getName());
        return "hechos-coleccion/lista";
    }

    @PostMapping("/solicitudes-eliminacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VISUALIZADOR', 'CONTRIBUYENTE')")
    public String crearSolicitudEliminacion(@ModelAttribute SolicitudDTO solicitud, RedirectAttributes redirectAttrs) {
        try {
            metamapaService.crearSolicitudEliminacion(solicitud);
            redirectAttrs.addFlashAttribute("mensaje", "Solicitud de eliminación creada correctamente.");
        } catch (Exception e) {
            log.error("Error al crear la solicitud de eliminación", e);
            redirectAttrs.addFlashAttribute("error", "Error al crear la solicitud de eliminación.");
        }
        return "redirect:/metamapa/colecciones";
    }
}

