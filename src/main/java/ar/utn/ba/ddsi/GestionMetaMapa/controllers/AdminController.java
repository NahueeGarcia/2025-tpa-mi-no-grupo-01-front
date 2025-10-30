package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import ar.utn.ba.ddsi.GestionMetaMapa.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/panel")
    public String panel(Model model) {
        // Obtenemos el resumen completo en una sola llamada
        ResumenDashboardDTO resumen = adminService.obtenerResumenDashboard();
        model.addAttribute("resumen", resumen);

        // También preparamos los objetos para los modales
        model.addAttribute("nuevaFuente", new FuenteDTO());

        return "admin/panel";
    }

    @GetMapping("/gestionar-colecciones")
    public String gestionarColecciones(Model model) {
        List<ColeccionDTO> colecciones = adminService.obtenerTodasLasColecciones();
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("nuevaFuente", new FuenteDTO());
        model.addAttribute("modificarAlgoritmo", new ModificarAlgoritmoDTO());
        return "admin/gestionar-colecciones"; // Sirve la nueva página de gestión
    }

    @GetMapping("/gestionar-solicitudes")
    public String gestionarSolicitudes(Model model) {
        List<SolicitudEliminacionDTO> solicitudes = adminService.obtenerSolicitudesDeEliminacion();
        model.addAttribute("solicitudes", solicitudes);
        return "admin/gestionar-solicitudes"; // Sirve la nueva página de gestión
    }

    // --- Endpoints de Acciones (POST) ---

    @PostMapping("/colecciones/{id}/fuentes/agregar")
    public String agregarFuente(@PathVariable("id") Long coleccionId,
                                @ModelAttribute("nuevaFuente") FuenteDTO fuenteDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.agregarFuenteAColeccion(coleccionId, fuenteDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente agregada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar la fuente: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones"; // Redirige a la página de gestión
    }

    @PostMapping("/colecciones/{id}/fuentes/{fuenteId}/quitar")
    public String quitarFuente(@PathVariable("id") Long coleccionId,
                               @PathVariable("fuenteId") Long fuenteId,
                               RedirectAttributes redirectAttributes) {
        try {
            adminService.quitarFuenteDeColeccion(coleccionId, fuenteId);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente quitada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al quitar la fuente: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones"; // Redirige a la página de gestión
    }

    @PostMapping("/colecciones/{id}/algoritmo/modificar")
    public String modificarAlgoritmo(@PathVariable("id") Long coleccionId,
                                     @ModelAttribute("modificarAlgoritmo") ModificarAlgoritmoDTO dto,
                                     RedirectAttributes redirectAttributes) {
        try {
            adminService.modificarAlgoritmoDeConsenso(coleccionId, dto.getTipo());
            redirectAttributes.addFlashAttribute("mensaje", "Algoritmo modificado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el algoritmo: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones"; // Redirige a la página de gestión
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId, RedirectAttributes redirectAttributes) {
        try {
            adminService.aprobarSolicitud(solicitudId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud aprobada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar la solicitud: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-solicitudes"; // Redirige a la página de gestión
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId, RedirectAttributes redirectAttributes) {
        try {
            adminService.rechazarSolicitud(solicitudId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud rechazada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar la solicitud: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-solicitudes"; // Redirige a la página de gestión
    }


    @GetMapping("/colecciones/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("coleccion", new ColeccionDTO());
        return "colecciones/crear";
    }

    @PostMapping("/colecciones/crear")
    public String crearColeccion(@ModelAttribute("coleccion") ColeccionDTO coleccionDTO,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminService.crearColeccion(coleccionDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Colección creada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la colección: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones";
    }

    @PostMapping("/colecciones/{id}/eliminar")
    public String eliminarColeccion(@PathVariable("id") Long coleccionId, RedirectAttributes redirectAttributes) {
        try {
            adminService.eliminarColeccion(coleccionId);
            redirectAttributes.addFlashAttribute("mensaje", "Colección eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la colección: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones";
    }
}