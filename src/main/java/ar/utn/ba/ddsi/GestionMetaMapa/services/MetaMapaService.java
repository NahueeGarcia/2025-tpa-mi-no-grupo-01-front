package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaMapaService {

    private final GestionMetaMapaApiService apiService;
    private final HttpSession httpSession;

    public MetaMapaService(GestionMetaMapaApiService apiService, HttpSession httpSession) {
        this.apiService = apiService;
        this.httpSession = httpSession;
    }

    private String getJwtToken() {
        return (String) httpSession.getAttribute("accessToken");
    }

    public List<HechoDTO> obtenerTodosLosHechos() {
        return apiService.obtenerHechos();
    }

    public List<ColeccionDTO> obtenerTodasLasColecciones() {
        return apiService.obtenerColecciones();
    }

    /**
     * Reincorporado: Obtiene los hechos de una colección específica.
     * Esta operación requiere que el usuario esté autenticado.
     */
    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion) {
        String token = getJwtToken();
        if (token == null) {
            // Si no hay usuario logueado, no se puede realizar esta consulta protegida.
            return List.of();
        }
        return apiService.obtenerHechosPorColeccion(id, navegacion, token);
    }

    public void crearSolicitudEliminacion(SolicitudDTO solicitud) {
        String token = getJwtToken();
        if (token != null) {
            apiService.crearSolicitud(solicitud, token);
        }
    }
}