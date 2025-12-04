package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.FuenteDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoEdicionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.ResumenDashboardDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudEliminacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final GestionMetaMapaApiService apiService;

    // Métodos para moderación de hechos
    public List<HechoDTO> listarHechosPendientes() {
        return apiService.listarHechosPendientes();
    }

    public void aprobarHecho(Long id) {
        apiService.aprobarHecho(id);
    }

    public void rechazarHecho(Long id) {
        apiService.rechazarHecho(id);
    }

    public void aceptarHechoConModificaciones(Long id, HechoEdicionDTO dto) {
        apiService.aceptarHechoConModificaciones(id, dto);
    }

    public List<ColeccionDTO> obtenerTodasLasColecciones() {
        return apiService.obtenerTodasLasColeccionesAdmin();
    }

    public void agregarFuenteAColeccion(Long coleccionId, FuenteDTO fuenteDTO) {
        apiService.agregarFuenteAColeccion(coleccionId, fuenteDTO);
    }

    public void quitarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        apiService.quitarFuenteDeColeccion(coleccionId, fuenteId);
    }

    public void modificarAlgoritmoDeConsenso(Long coleccionId, String tipoAlgoritmo) {
        apiService.modificarAlgoritmoDeConsenso(coleccionId, tipoAlgoritmo);
    }

    public List<SolicitudEliminacionDTO> obtenerSolicitudesDeEliminacion() {
        return apiService.obtenerSolicitudesDeEliminacionAdmin();
    }

    public void procesarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        apiService.procesarFuenteDeColeccion(coleccionId, fuenteId);
    }


    public void aprobarSolicitud(Long solicitudId) {
        apiService.aprobarSolicitud(solicitudId);
    }

    public void rechazarSolicitud(Long solicitudId) {
        apiService.rechazarSolicitud(solicitudId);
    }

    public ResumenDashboardDTO obtenerResumenDashboard() {
        return apiService.obtenerResumenDashboard();
    }

    public ColeccionDTO crearColeccion(ColeccionDTO dto) {
        return apiService.crearColeccion(dto);
    }

    public void eliminarColeccion(Long coleccionId) {
        apiService.eliminarColeccion(coleccionId);
    }

    public ColeccionDTO obtenerColeccionPorId(Long coleccionId) {
        return apiService.obtenerColeccionPorId(coleccionId);
    }

    public void modificarColeccion(Long coleccionId, ColeccionDTO coleccionDTO) {
        apiService.modificarColeccion(coleccionId, coleccionDTO);
    }

    public void crearFuenteDataset(FuenteDTO fuenteDTO) {
        apiService.crearFuenteDataset(fuenteDTO);
    }

    public List<FuenteDTO> obtenerTodasLasFuentes() {
        return apiService.obtenerTodasLasFuentes();
    }

    public void eliminarFuente(Long fuenteId) {
        apiService.eliminarFuente(fuenteId);
    }
}