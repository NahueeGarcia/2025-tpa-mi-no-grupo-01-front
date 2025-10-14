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
    public String listarHechos(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size) {

        // 1. Obtenemos la lista COMPLETA desde el backend (como antes)
        List<HechoDTO> todosLosHechos = metamapaService.obtenerTodosLosHechos();

        // 2. Calculamos la paginación en el servidor del frontend
        int totalHechos = todosLosHechos.size();
        int totalPages = (totalHechos + size - 1) / size; // Cálculo de paginación seguro

        int start = page * size;
        int end = Math.min(start + size, totalHechos);

        List<HechoDTO> hechosPaginados = List.of();
        if (start < totalHechos) {
            hechosPaginados = todosLosHechos.subList(start, end);
        }

        // 3. Pasamos todos los datos necesarios a la vista
        model.addAttribute("hechos", hechosPaginados);
        model.addAttribute("titulo", "Listado de Hechos");
        model.addAttribute("totalHechos", totalHechos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        return "hechos/lista";
    }


    @GetMapping("/colecciones")
    public String listarColecciones(Model model,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {

        List<ColeccionDTO> todasLasColecciones = metamapaService.obtenerTodasLasColecciones();

        int totalColecciones = todasLasColecciones.size();
        int totalPages = (totalColecciones + size - 1) / size;

        int start = page * size;
        int end = Math.min(start + size, totalColecciones);

        List<ColeccionDTO> coleccionesPaginadas = List.of();
        if (start < totalColecciones) {
            coleccionesPaginadas = todasLasColecciones.subList(start, end);
        }

        model.addAttribute("colecciones", coleccionesPaginadas);
        model.addAttribute("titulo", "Listado de Colecciones");
        model.addAttribute("totalColecciones", totalColecciones);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

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

