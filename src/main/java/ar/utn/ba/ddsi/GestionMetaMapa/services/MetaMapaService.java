package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudEliminacionDTO;
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

    public List<HechoDTO> obtenerMisHechos() {
        return apiService.obtenerMisHechos();
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

    public void crearSolicitudEliminacion(SolicitudEliminacionDTO solicitud){

        String token = null;


        //String token = getJwtToken();
        //if (token != null) {
        //    apiService.crearSolicitud(solicitud, token);
        //}
        System.out.println("[DEBUG] Enviando solicitud de eliminación al backend para Hecho ID: " + solicitud.getHechoId());

        apiService.crearSolicitud(solicitud, token);

        System.out.println("[DEBUG] Solicitud enviada al backend sin errores en el frontend.");
    }


    public void editarHecho(Long id, HechoDTO dto) {
        apiService.editarHecho(id, dto);
    }

    public void crearHecho(HechoDTO dto) {
        apiService.crearHecho(dto);
    }

    public HechoDTO obtenerHechoPorId(Long id) {
        return apiService.obtenerHechoPorId(id);
    }
}