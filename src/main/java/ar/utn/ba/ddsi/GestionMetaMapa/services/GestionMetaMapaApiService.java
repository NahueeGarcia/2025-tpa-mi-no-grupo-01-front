package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import jakarta.servlet.http.HttpServletRequest; // Importar HttpServletRequest
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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

    // Método auxiliar para obtener el token JWT de la sesión
    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession(false); // No crear si no existe
            if (session != null) {
                return (String) session.getAttribute("jwt_token");
            }
        }
        return null;
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

    // --- MÉTODOS PROTEGIDOS  ---
    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion, String
            accessToken) {
        String url = metamapaServiceUrl + "/colecciones/" + id + "/hechos?navegacion=" +
                navegacion;
        return this.webClient.get().uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();
    }

    public void crearSolicitud(SolicitudEliminacionDTO solicitudDTO, String accessToken) {
        // 1. Construir la petición POST base
        WebClient.RequestHeadersSpec<?> requestSpec = this.webClient.post()
                .uri(metamapaServiceUrl + "/solicitudes-eliminacion")
                .bodyValue(solicitudDTO);

        // 2. Añadir la cabecera de autorización SÓLO si el token no es nulo
        if (accessToken != null && !accessToken.isBlank()) {
            requestSpec.header("Authorization", "Bearer " + accessToken);
        }

        // 3. Ejecutar la petición
        requestSpec.retrieve()
                   .bodyToMono(Void.class)
                   .block();
    }

    // --- MÉTODOS DE ADMINISTRACIÓN ---

    public List<ColeccionDTO> obtenerTodasLasColeccionesAdmin() {
        String token = getJwtToken();
        if (token == null) {
            // Manejar el caso donde no hay token, quizás lanzar una excepción o devolver lista vacía
            return List.of();
        }
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/colecciones")
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToFlux(ColeccionDTO.class).collectList().block();
    }

    public void agregarFuenteAColeccion(Long coleccionId, FuenteDTO fuenteDTO) {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId +
                        "/fuentes")
                .header("Authorization", "Bearer " + token)
                .bodyValue(fuenteDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void quitarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        this.webClient.delete().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId +
                        "/fuentes/" + fuenteId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void modificarAlgoritmoDeConsenso(Long coleccionId, String tipoAlgoritmo) {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        ModificarAlgoritmoDTO dto = new ModificarAlgoritmoDTO(tipoAlgoritmo);
        this.webClient.put().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId +
                        "/algoritmo")
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<SolicitudEliminacionDTO> obtenerSolicitudesDeEliminacionAdmin() {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return List.of(); }
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/solicitudes-eliminacion")
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToFlux(SolicitudEliminacionDTO.class).collectList().block();
    }

    public void procesarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId +
                        "/fuentes/" + fuenteId + "/procesar")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }


    public void aprobarSolicitud(Long solicitudId) {
        String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        this.webClient.post().uri(metamapaServiceUrl + "/admin/solicitudes/" + solicitudId + "/aprobar")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void rechazarSolicitud(Long solicitudId) {
         String token = getJwtToken();
        if (token == null) { /* Manejar error */ return; }
        this.webClient.post().uri(metamapaServiceUrl + "/admin/solicitudes/" + solicitudId + "/rechazar")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ResumenDashboardDTO obtenerResumenDashboard() {
        String token = getJwtToken();
         if (token == null) { return new ResumenDashboardDTO(); }
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/resumen-dashboard")
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(ResumenDashboardDTO.class).block();
    }

    public ColeccionDTO crearColeccion(ColeccionDTO dto) {
        String token = getJwtToken();
        if (token == null) { throw new RuntimeException("No autenticado"); }
        return this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones")
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ColeccionDTO.class)
                .block();
    }

    public void eliminarColeccion(Long coleccionId) {
        String token = getJwtToken();
        if (token == null) { throw new RuntimeException("No autenticado"); }
        this.webClient.delete()
               .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ColeccionDTO obtenerColeccionPorId(Long coleccionId) {
        String token = getJwtToken();
        if (token == null) { throw new RuntimeException("No autenticado"); }
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(ColeccionDTO.class)
                .block();
    }

    public void modificarColeccion(Long coleccionId, ColeccionDTO dto) {
        String token = getJwtToken();
        if (token == null) { throw new RuntimeException("No autenticado"); }
        this.webClient.put()
                .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void crearFuenteDataset(FuenteDTO dto) {
        String token = getJwtToken();
        if (token == null) { throw new RuntimeException("No autenticado"); }
        this.webClient.post()
                .uri(metamapaServiceUrl + "/admin/fuentes")
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<FuenteDTO> obtenerTodasLasFuentes() {
        String token = getJwtToken();
        if (token == null) { return List.of(); }
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/admin/fuentes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(FuenteDTO.class)
                .collectList()
                .block();
    }

    public HechoDTO obtenerHechoPorId(Long id) {
        // Este endpoint es público, no necesita token de autorización
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/hechos/" + id)
                .retrieve()
                .bodyToMono(HechoDTO.class)
                .block();
    }
}