package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.FuenteDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.ResumenDashboardDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudEliminacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final GestionMetaMapaApiService apiService;

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

}