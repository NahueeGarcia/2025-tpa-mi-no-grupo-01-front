package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import jakarta.servlet.http.HttpSession; // Importamos HttpSession
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MetaMapaService {

    private final WebClient webClient;
    private final HttpSession httpSession; // Inyectamos la sesión HTTP

    // Modificamos el constructor para recibir la sesión
    public MetaMapaService(HttpSession httpSession) {
        this.httpSession = httpSession;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8089/api")
                .build();
    }

    private String getJwtToken() {
        // Método privado para obtener el token guardado en la sesión
        return (String) httpSession.getAttribute("jwt_token");
    }

    // --- MÉTODO PARA HECHOS (AHORA CON AUTENTICACIÓN) ---
    public List<HechoDTO> obtenerTodosLosHechos() {
        String token = getJwtToken();
        if (token == null) {
            // Si no hay token, no podemos hacer la petición
            return List.of();
        }

        Mono<List<HechoDTO>> mono = this.webClient.get()
                .uri("/hechos")
                .header("Authorization", "Bearer " + token) // <-- ¡LA LÍNEA CLAVE!
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<HechoDTO>>() {});

        return mono.block();
    }

    // --- MÉTODO PARA COLECCIONES (TAMBIÉN CON AUTENTICACIÓN) ---
    public List<ColeccionDTO> obtenerTodasLasColecciones() {
        String token = getJwtToken();
        if (token == null) {
            // Si no hay token, no podemos hacer la petición.
            // Devolver una lista vacía es una forma segura de manejarlo.
            return List.of();
        }

        Mono<List<ColeccionDTO>> mono = this.webClient.get()
                .uri("/colecciones") // Llama a /api/colecciones del Gateway
                .header("Authorization", "Bearer " + token) // Adjunta el token
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ColeccionDTO>>() {});

        return mono.block();
    }

    // --- OTROS MÉTODOS ---
    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion) {
        return List.of();
    }

    public void crearSolicitudEliminacion(SolicitudDTO solicitud) {
    }
}