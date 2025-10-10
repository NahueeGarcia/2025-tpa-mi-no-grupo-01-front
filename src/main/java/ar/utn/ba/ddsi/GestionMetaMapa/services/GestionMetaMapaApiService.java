package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import ar.utn.ba.ddsi.GestionMetaMapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.GestionMetaMapa.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class GestionMetaMapaApiService {
    private static final Logger log = LoggerFactory.getLogger(GestionMetaMapaApiService.class);
    private final WebClient webClient;
    private final WebApiCallerService webApiCallerService;
    private final String authServiceUrl;
    private final String metamapaServiceUrl;

    @Autowired
    public GestionMetaMapaApiService(
            WebApiCallerService webApiCallerService,
            @Value("${auth.service.url}") String authServiceUrl,
            @Value("${metamapa.service.url}") String metamapaServiceUrl) {
        this.webClient = WebClient.builder().build();
        this.webApiCallerService = webApiCallerService;
        this.authServiceUrl = authServiceUrl;
        this.metamapaServiceUrl = metamapaServiceUrl;
    }

    public AuthResponseDTO login(String username, String password) {
        try {
            AuthResponseDTO response = webClient
                    .post()
                    .uri(authServiceUrl + "/auth")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
            return response;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Login fallido - credenciales incorrectas
                return null;
            }
            // Otros errores HTTP
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }

    public RolesPermisosDTO getRolesPermisos(String accessToken) {
        try {
            RolesPermisosDTO response = webApiCallerService.getWithAuth(
                    authServiceUrl + "/auth/user/roles-permisos",
                    accessToken,
                    RolesPermisosDTO.class
            );
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
        }
    }

    public List<HechoDTO> obtenerHechos() {
        List<HechoDTO> response = webApiCallerService.getList(metamapaServiceUrl + "/hechos", HechoDTO.class);
        return response != null ? response : List.of();
    }

    public List<ColeccionDTO> obtenerColecciones() {
        List<ColeccionDTO> response = webApiCallerService.getList(metamapaServiceUrl + "/colecciones", ColeccionDTO.class);
        return response != null ? response : List.of();
    }

    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion) {
        List<HechoDTO> response = webApiCallerService.getList(metamapaServiceUrl + "/colecciones/" + id + "/hechos", HechoDTO.class);
        return response != null ? response : List.of();
    }

    public void crearSolicitud(SolicitudDTO solicitudDTO) {
        webApiCallerService.post(metamapaServiceUrl + "/solicitudes-eliminacion", solicitudDTO, Void.class);
    }
}
