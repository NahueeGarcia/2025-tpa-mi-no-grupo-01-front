package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.FuenteDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.ModificarAlgoritmoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudEliminacionDTO;
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
@PreAuthorize("hasRole('ADMIN')") // Proteger toda la clase
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/panel")
    public String panel(Model model) {
        List<ColeccionDTO> colecciones = adminService.obtenerTodasLasColecciones();
        List<SolicitudEliminacionDTO> solicitudes =
                adminService.obtenerSolicitudesDeEliminacion(); // Obtener solicitudes
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("solicitudes", solicitudes); // Añadir solicitudes al modelo
        model.addAttribute("nuevaFuente", new FuenteDTO());
        model.addAttribute("modificarAlgoritmo", new ModificarAlgoritmoDTO()); // Para el  formulario de modificar algoritmo
        return "admin/panel";
    }

    @PostMapping("/colecciones/{id}/fuentes/agregar")
    public String agregarFuente(@PathVariable("id") Long coleccionId,
                                @ModelAttribute("nuevaFuente") FuenteDTO fuenteDTO, // Usar el nombre del atributo del modelo
                                        RedirectAttributes redirectAttributes) {
        try {
            adminService.agregarFuenteAColeccion(coleccionId, fuenteDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente agregada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar la fuente: " +
                    e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/colecciones/{id}/fuentes/{fuenteId}/quitar")
    public String quitarFuente(@PathVariable("id") Long coleccionId,
                               @PathVariable("fuenteId") Long fuenteId,
                               RedirectAttributes redirectAttributes) {
        try {
            adminService.quitarFuenteDeColeccion(coleccionId, fuenteId);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente quitada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al quitar la fuente: " +
                    e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/colecciones/{id}/algoritmo/modificar")
    public String modificarAlgoritmo(@PathVariable("id") Long coleccionId,
                                     @ModelAttribute("modificarAlgoritmo") ModificarAlgoritmoDTO
                                             dto,
                                     RedirectAttributes redirectAttributes) {
        try {
            adminService.modificarAlgoritmoDeConsenso(coleccionId, dto.getTipo());
            redirectAttributes.addFlashAttribute("mensaje", "Algoritmo modificado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el algoritmo: " +
                    e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/colecciones/{idColeccion}/fuentes/{idFuente}/procesar")
    public String procesarFuente(@PathVariable("idColeccion") Long coleccionId,
                                 @PathVariable("idFuente") Long fuenteId,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminService.procesarFuenteDeColeccion(coleccionId, fuenteId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud de procesamiento de fuente enviada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la fuente: " +
                    e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId, RedirectAttributes redirectAttributes) {
        try {
            adminService.aprobarSolicitud(solicitudId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud aprobada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar la solicitud: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId, RedirectAttributes redirectAttributes) {
        try {
            adminService.rechazarSolicitud(solicitudId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud rechazada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar la solicitud: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }}