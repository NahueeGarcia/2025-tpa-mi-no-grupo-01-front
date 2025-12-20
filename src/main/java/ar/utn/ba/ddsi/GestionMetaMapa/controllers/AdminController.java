package ar.utn.ba.ddsi.GestionMetaMapa.controllers;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import ar.utn.ba.ddsi.GestionMetaMapa.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;




    @GetMapping("/panel")
    public String panel(Model model) {
        try {
            // Obtenemos el resumen completo en una sola llamada
            ResumenDashboardDTO resumen = adminService.obtenerResumenDashboard();
            model.addAttribute("resumen", resumen);
        } catch (WebClientResponseException.TooManyRequests e) {
            model.addAttribute("error", "⚠️ Ha realizado demasiadas peticiones. Por favor espere un minuto antes de recargar.");
            model.addAttribute("resumen", new ResumenDashboardDTO()); // Objeto vacío para evitar NullPointer en vista
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el panel: " + e.getMessage());
            model.addAttribute("resumen", new ResumenDashboardDTO());
        }

        // También preparamos los objetos para los modales
        model.addAttribute("nuevaFuente", new FuenteDTO());

        return "admin/panel";
    }

    @GetMapping("/gestionar-colecciones")
    public String gestionarColecciones(Model model) {
        List<ColeccionDTO> colecciones = adminService.obtenerTodasLasColecciones();
        List<FuenteDTO> todasLasFuentes = adminService.obtenerTodasLasFuentes(); // <-- NUEVA LÍNEA
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("todasLasFuentes", todasLasFuentes); // <-- NUEVA LÍNEA
        model.addAttribute("nuevaFuente", new FuenteDTO());
        model.addAttribute("modificarAlgoritmo", new ModificarAlgoritmoDTO());
        return "admin/gestionar-colecciones";
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

    @PostMapping("/colecciones/{idColeccion}/fuentes/{idFuente}/procesar")
    public String procesarFuente(@PathVariable("idColeccion") Long coleccionId,
                                 @PathVariable("idFuente") Long fuenteId,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminService.procesarFuenteDeColeccion(coleccionId, fuenteId);
            redirectAttributes.addFlashAttribute("mensaje", "Procesamiento de la fuente iniciado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la fuente: " + e.getMessage());
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

    // --- Métodos para Editar Colección ---

    @GetMapping("/colecciones/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable("id") Long coleccionId, Model model) {
        System.out.println("[DEBUG] AdminController: Petición GET a /admin/colecciones/" + coleccionId + "/editar RECIBIDA.");
        try {
            ColeccionDTO coleccion = adminService.obtenerColeccionPorId(coleccionId);
            model.addAttribute("coleccion", coleccion);
            System.out.println("[DEBUG] AdminController: Mostrando formulario de edición para la colección: " + coleccion.getTitulo());
            return "colecciones/editar";
        } catch (Exception e) {
            System.err.println("[ERROR] AdminController: Error al obtener la colección para editar. Causa: " + e.getMessage());
            return "redirect:/admin/gestionar-colecciones";
        }
    }

    @PostMapping("/colecciones/{id}/editar")
    public String modificarColeccion(@PathVariable("id") Long coleccionId,
                                     @ModelAttribute("coleccion") ColeccionDTO coleccionDTO,
                                     RedirectAttributes redirectAttributes) {
        System.out.println("[DEBUG] AdminController: Petición POST a /admin/colecciones/" + coleccionId + "/editar RECIBIDA.");
        try {
            adminService.modificarColeccion(coleccionId, coleccionDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Colección modificada exitosamente.");
            System.out.println("[DEBUG] AdminController: Colección modificada exitosamente.");
        } catch (Exception e) {
            System.err.println("[ERROR] AdminController: Error al modificar la colección. Causa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al modificar la colección: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-colecciones";
    }

    @PostMapping("/fuentes/crear-dataset")
    public String crearFuenteDataset(@ModelAttribute("nuevaFuente") FuenteDTO fuenteDTO,
                                       RedirectAttributes redirectAttributes) {
        try {
            adminService.crearFuenteDataset(fuenteDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente Dataset creada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la fuente: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/gestionar-fuentes")
    public String gestionarFuentes(Model model) {
        List<FuenteDTO> fuentes = adminService.obtenerTodasLasFuentes();
        model.addAttribute("fuentes", fuentes);
        return "admin/gestionar-fuentes";
    }

    @PostMapping("/fuentes/{id}/eliminar")
    public String eliminarFuente(@PathVariable("id") Long fuenteId, RedirectAttributes redirectAttributes) {
        try {
            adminService.eliminarFuente(fuenteId);
            redirectAttributes.addFlashAttribute("mensaje", "Fuente eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la fuente: " + e.getMessage());
        }
        return "redirect:/admin/gestionar-fuentes";
    }

    // --- Métodos para Moderación de Hechos ---

    @GetMapping("/moderacion-hechos")
    public String moderarHechos(Model model) {
        List<HechoDTO> hechosPendientes = adminService.listarHechosPendientes();
        model.addAttribute("hechosPendientes", hechosPendientes);
        return "admin/moderacion-hechos"; // Nueva vista
    }

    @PostMapping("/hechos/{id}/aprobar")
    public String aprobarHecho(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.aprobarHecho(id);
            redirectAttributes.addFlashAttribute("mensaje", "Hecho aprobado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar el hecho: " + e.getMessage());
        }
        return "redirect:/admin/moderacion-hechos";
    }

    @PostMapping("/hechos/{id}/rechazar")
    public String rechazarHecho(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.rechazarHecho(id);
            redirectAttributes.addFlashAttribute("mensaje", "Hecho rechazado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar el hecho: " + e.getMessage());
        }
        return "redirect:/admin/moderacion-hechos";
    }

    @GetMapping("/hechos/{id}/editar-y-aceptar")
    public String mostrarFormularioEditarAceptar(@PathVariable("id") Long id, Model model) {
        HechoDTO hechoOriginal = adminService.obtenerHechoPorIdOrigen(id);

        // Crear el DTO específico para la edición y poblarlo
        HechoEdicionDTO hechoAEditar = new HechoEdicionDTO();
        hechoAEditar.setTitulo(hechoOriginal.getTitulo());
        hechoAEditar.setDescripcion(hechoOriginal.getDescripcion());
        hechoAEditar.setCategoria(hechoOriginal.getCategoria());
        hechoAEditar.setLatitud(hechoOriginal.getLatitud());
        hechoAEditar.setLongitud(hechoOriginal.getLongitud());
        if (hechoOriginal.getFecAcontecimiento() != null) {
            hechoAEditar.setFechaAcontecimiento(hechoOriginal.getFecAcontecimiento().toString());
        }

        model.addAttribute("hechoAEditar", hechoAEditar);
        model.addAttribute("idOrigen", id); // Pasar el idOrigen para construir la URL del form
        return "admin/editar-hecho-moderacion";
    }

    @PostMapping("/hechos/{id}/modificar-y-aceptar")
    public String modificarYConfirmarHecho(@PathVariable("id") Long id,
                                           @ModelAttribute("hechoAEditar") HechoEdicionDTO hechoModificado,
                                           RedirectAttributes redirectAttributes) {
        try {
            adminService.aceptarHechoConModificaciones(id, hechoModificado);
            redirectAttributes.addFlashAttribute("mensaje", "Hecho modificado y aceptado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar y aceptar el hecho: " + e.getMessage());
        }
        return "redirect:/admin/moderacion-hechos";
    }

    @PostMapping("/evaluacion-consensos")
    public String forzarEvaluacionConsensos(RedirectAttributes redirectAttributes) {
        try {
            adminService.forzarEvaluacionConsensos();
            redirectAttributes.addFlashAttribute("mensaje", "Evaluación de consensos ejecutada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar la evaluación de consensos: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/refrescar-colecciones")
    public String forzarRefrescoColecciones(RedirectAttributes redirectAttributes) {
        try {
            adminService.forzarRefrescoColecciones();
            redirectAttributes.addFlashAttribute("mensaje", "Refresco de colecciones ejecutado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al refrescar colecciones: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }
}