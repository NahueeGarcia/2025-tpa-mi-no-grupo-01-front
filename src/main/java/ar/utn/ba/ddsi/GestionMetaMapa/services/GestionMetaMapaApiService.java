package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GestionMetaMapaApiService {

    private final WebClient webClient;
    private final String authServiceUrl;
    private final String metamapaServiceUrl;

    public GestionMetaMapaApiService(
            @Value("${auth.service.url}") String authServiceUrl,
            @Value("${metamapa.service.url}") String metamapaServiceUrl) {
        this.webClient = WebClient.builder().build();
        this.authServiceUrl = authServiceUrl;
        this.metamapaServiceUrl = metamapaServiceUrl;
    }

    // --- MÉTODOS DE AUTENTICACIÓN ---
    public AuthResponseDTO login(String username, String password) {
        Map<String, String> body = Map.of("username", username, "password", password);
        return this.webClient.post().uri(authServiceUrl + "/auth").bodyValue(body)
                .retrieve().bodyToMono(AuthResponseDTO.class).block();
    }

    public RolesPermisosDTO getRolesPermisos(String accessToken) {
        return this.webClient.get().uri(authServiceUrl + "/auth/user/roles-permisos")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().bodyToMono(RolesPermisosDTO.class).block();
    }

    // --- MÉTODOS PÚBLICOS ---
    public List<HechoDTO> obtenerHechos() {
        return this.webClient.get().uri(metamapaServiceUrl + "/hechos")
                .retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }

    public List<ColeccionDTO> obtenerColecciones() {
        return this.webClient.get().uri(metamapaServiceUrl + "/colecciones")
                .retrieve().bodyToFlux(ColeccionDTO.class).collectList().block();
    }

    // --- MÉTODOS PROTEGIDOS ---

    /**
     * Obtiene los hechos de una colección específica. Requiere autenticación.
     */
    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion, String accessToken) {
        String url = metamapaServiceUrl + "/colecciones/" + id + "/hechos?navegacion=" + navegacion;
        return this.webClient.get().uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();
    }

    public void crearSolicitud(SolicitudDTO solicitudDTO, String accessToken) {
        this.webClient.post().uri(metamapaServiceUrl + "/solicitudes-eliminacion")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(solicitudDTO)
                .retrieve().bodyToMono(Void.class).block();
    }
}