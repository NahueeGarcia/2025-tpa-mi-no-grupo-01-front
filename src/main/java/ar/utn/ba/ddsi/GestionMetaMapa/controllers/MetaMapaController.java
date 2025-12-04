package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudEliminacionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.GestionMetaMapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.providers.CustomAuthProvider;
import ar.utn.ba.ddsi.GestionMetaMapa.services.MetaMapaService;
import jakarta.servlet.http.HttpSession;
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

import java.time.LocalDateTime;
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
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(required = false) String q) {




        // 1. Obtenemos la lista COMPLETA desde el backend (como antes)
        List<HechoDTO> todosLosHechos = metamapaService.obtenerTodosLosHechos();


        if (q != null && !q.trim().isEmpty()) {
            final String queryBusqueda = q.toLowerCase();
            todosLosHechos = todosLosHechos.stream()
                    .filter(hecho -> hecho.getTitulo().toLowerCase().contains(queryBusqueda))
                    .collect(java.util.stream.Collectors.toList());
        }


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
        model.addAttribute("query", q);

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
    public String listarHechosPorColeccion(@PathVariable("id") Long id, @RequestParam(name = "navegacion", defaultValue = "CURADA") String navegacion, Model model, HttpSession session) {
        List<HechoDTO> hechos = metamapaService.obtenerHechosPorColeccion(id, navegacion);
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Listado de hechos por colección");
        model.addAttribute("totalDeHechos", hechos.size());
        
        Long currentUserId = (Long) session.getAttribute("currentUserId");
        model.addAttribute("currentUserId", currentUserId);

        return "hechos-coleccion/lista";
    }

    @PostMapping("/solicitudes-eliminacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VISUALIZADOR', 'CONTRIBUYENTE') or isAnonymous()")
    public String crearSolicitudEliminacion(@ModelAttribute SolicitudEliminacionDTO solicitud, RedirectAttributes redirectAttrs) {
        try {
            metamapaService.crearSolicitudEliminacion(solicitud);
            redirectAttrs.addFlashAttribute("mensaje", "Solicitud de eliminación creada correctamente.");
        } catch (Exception e) {
            log.error("Error al crear la solicitud de eliminación", e);
            redirectAttrs.addFlashAttribute("error", "Error al crear la solicitud de eliminación.");
        }
        return "redirect:/metamapa/hechos";
    }

    @PreAuthorize("permitAll")
    @GetMapping("/hechos/{id}")
    public String verDetalleHecho(@PathVariable("id") Long id, Model model, HttpSession session) {
        System.out.println("[DEBUG] MetaMapaController.verDetalleHecho - Petición RECIBIDA para el Hecho ID: " + id);
        try {
            HechoDTO hecho = metamapaService.obtenerHechoPorId(id);
            model.addAttribute("hecho", hecho);

            Long currentUserId = (Long) session.getAttribute("currentUserId");
            model.addAttribute("currentUserId", currentUserId);

            System.out.println("[DEBUG] MetaMapaController.verDetalleHecho - Devolviendo vista 'hechos/detalle' para el Hecho: " +
                    hecho.getTitulo());
            return "hechos/detalle";
        } catch (Exception e) {
            System.err.println("[DEBUG] MetaMapaController.verDetalleHecho - ERROR al obtener el Hecho ID: " + id + ". Causa: " +
                    e.getMessage());
                // Manejo básico de error si el hecho no se encuentra
                return "redirect:/"; // Redirige a la home si hay un error
        }
    }


    @GetMapping("/mapa")
    public String verMapaDeHechos(Model model) {
        List<HechoDTO> todosLosHechos = metamapaService.obtenerTodosLosHechos();
        model.addAttribute("hechos", todosLosHechos);
        model.addAttribute("titulo", "Mapa de Hechos");
        return "mapa/vista";
    }

    @GetMapping("/hechos/nuevo")
    public String mostrarFormularioCrearHecho(Model model) {
        model.addAttribute("hecho", new HechoDTO());
        return "hechos/crear";
    }

    @PostMapping("/hechos/crear")
    public String crearHecho(@ModelAttribute("hecho") HechoDTO hecho, RedirectAttributes redirectAttrs) {
        try {
            metamapaService.crearHecho(hecho);
            redirectAttrs.addFlashAttribute("mensaje", "Hecho aportado correctamente. Gracias por contribuir.");
        } catch (Exception e) {
            log.error("Error al crear el hecho", e);
            redirectAttrs.addFlashAttribute("error", "No se pudo aportar el hecho.");
        }
        return "redirect:/metamapa/mapa";
    }

    @GetMapping("/hechos/{id}/editar")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioEditarHecho(@PathVariable("id") Long id, Model model, HttpSession session) {
        HechoDTO hecho = metamapaService.obtenerHechoPorId(id);
        Long currentUserId = (Long) session.getAttribute("currentUserId");

        // Doble chequeo de seguridad: en la vista y en el controlador
        if(hecho.getIdUsuarioCreador() == null || !hecho.getIdUsuarioCreador().equals(currentUserId)) {
            return "redirect:/403"; // Acceso denegado
        }

        model.addAttribute("hecho", hecho);
        return "hechos/editar";
    }

    @PostMapping("/hechos/{id}/editar")
    @PreAuthorize("isAuthenticated()")
    public String editarHecho(@PathVariable("id") Long id, @ModelAttribute("hecho") HechoDTO hecho, RedirectAttributes redirectAttrs) {
        try {
            metamapaService.editarHecho(id, hecho);
            redirectAttrs.addFlashAttribute("mensaje", "Hecho modificado correctamente.");
        } catch (Exception e) {
            log.error("Error al editar el hecho", e);
            redirectAttrs.addFlashAttribute("error", "No se pudo modificar el hecho: " + e.getMessage());
        }
        return "redirect:/metamapa/hechos/" + id;
    }
}

