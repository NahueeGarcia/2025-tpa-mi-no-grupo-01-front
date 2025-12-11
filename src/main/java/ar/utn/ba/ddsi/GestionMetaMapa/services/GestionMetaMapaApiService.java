package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class GestionMetaMapaApiService {

    private static final Logger log = LoggerFactory.getLogger(GestionMetaMapaApiService.class);
    private final WebClient webClient;
    private final String authServiceUrl;
    private final String metamapaServiceUrl;

    public GestionMetaMapaApiService(
            @Value("${auth.service.url}") String authServiceUrl,
            @Value("${metamapa.service.url}") String metamapaServiceUrl) {
        this.webClient = WebClient.builder()
                .filter(addAuthorizationHeader())
                .build();
        this.authServiceUrl = authServiceUrl;
        this.metamapaServiceUrl = metamapaServiceUrl;
    }

    private ExchangeFilterFunction addAuthorizationHeader() {
        return (clientRequest, next) -> {
            String token = getJwtToken();
            if (token != null && !token.isBlank()) {
                ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                        .header("Authorization", "Bearer " + token)
                        .build();
                return next.exchange(authorizedRequest);
            }
            return next.exchange(clientRequest);
        };
    }

    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession(false);
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
        // Este método es especial porque recibe el token directamente, no lo saca de la sesión
        return this.webClient.get().uri(authServiceUrl + "/auth/user/roles-permisos")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().bodyToMono(RolesPermisosDTO.class).block();
    }

    // --- MÉTODOS PÚBLICOS (NO REQUIEREN TOKEN) ---
    public List<HechoDTO> obtenerHechos() {
        return this.webClient.get().uri(metamapaServiceUrl + "/hechos")
                .retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }

    public List<ColeccionDTO> obtenerColecciones() {
        return this.webClient.get().uri(metamapaServiceUrl + "/colecciones")
                .retrieve().bodyToFlux(ColeccionDTO.class).collectList().block();
    }
    
    public HechoDTO obtenerHechoPorId(Long id) {
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/hechos/" + id)
                .retrieve()
                .bodyToMono(HechoDTO.class)
                .block();
    }

    public HechoDTO obtenerHechoPorIdOrigen(Long idOrigen) {
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/hechos/por-id-origen/" + idOrigen)
                .retrieve()
                .bodyToMono(HechoDTO.class)
                .block();
    }

    public void registrarUsuario(SignupDTO registroDTO) {
        this.webClient.post()
                .uri(authServiceUrl + "/auth/register")
                .bodyValue(registroDTO)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        response -> response.bodyToMono(String.class).map(msg -> new RuntimeException("Error en registro: " + msg))
                )
                .bodyToMono(Void.class)
                .block();
    }

    // --- MÉTODOS QUE PUEDEN SER ANÓNIMOS O AUTENTICADOS ---
    
    public void crearHecho(HechoDTO dto) {
        log.info("[DEBUG] Enviando DTO para crear hecho al gateway: {}", dto.toString());
        this.webClient.post()
                .uri(metamapaServiceUrl + "/hechos")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void crearSolicitud(SolicitudEliminacionDTO solicitudDTO) {
        this.webClient.post()
                .uri(metamapaServiceUrl + "/solicitudes-eliminacion")
                .bodyValue(solicitudDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // --- MÉTODOS ESTRICTAMENTE AUTENTICADOS ---

    public void editarHecho(Long id, HechoDTO dto) {
        this.webClient.put()
                .uri(metamapaServiceUrl + "/hechos/" + id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<HechoDTO> obtenerMisHechos() {
        return this.webClient.get().uri(metamapaServiceUrl + "/mis-hechos")
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();
    }

    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion, String categoria, String fechaInicio, String fechaFin, String ubicacion) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(metamapaServiceUrl + "/colecciones/{id}/hechos")
                .queryParam("navegacion", navegacion);

        if (categoria != null && !categoria.isBlank()) builder.queryParam("categoria", categoria);
        if (fechaInicio != null && !fechaInicio.isBlank()) builder.queryParam("fechaInicio", fechaInicio);
        if (fechaFin != null && !fechaFin.isBlank()) builder.queryParam("fechaFin", fechaFin);
        if (ubicacion != null && !ubicacion.isBlank()) builder.queryParam("ubicacion", ubicacion);

        URI uri = builder.encode().buildAndExpand(id).toUri();

        return this.webClient.get().uri(uri)
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();
    }

    // --- MÉTODOS DE ADMINISTRACIÓN (REQUIEREN TOKEN) ---

    public List<HechoDTO> listarHechosPendientes() {
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/moderacion/hechos/pendientes")
                .retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }

    public void aprobarHecho(Long id) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/moderacion/hechos/{id}/aprobar", id)
                .retrieve().bodyToMono(Void.class).block();
    }

    public void rechazarHecho(Long id) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/moderacion/hechos/{id}/rechazar", id)
                .retrieve().bodyToMono(Void.class).block();
    }
    
    public void aceptarHechoConModificaciones(Long id, HechoEdicionDTO dto) {
        this.webClient.put().uri(metamapaServiceUrl + "/admin/moderacion/hechos/{id}/modificar-y-aceptar", id)
                .bodyValue(dto)
                .retrieve().bodyToMono(Void.class).block();
    }
    
    public List<ColeccionDTO> obtenerTodasLasColeccionesAdmin() {
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/colecciones")
                .retrieve().bodyToFlux(ColeccionDTO.class).collectList().block();
    }

    public void agregarFuenteAColeccion(Long coleccionId, FuenteDTO fuenteDTO) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId + "/fuentes")
                .bodyValue(fuenteDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void quitarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        this.webClient.delete().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId + "/fuentes/" + fuenteId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void modificarAlgoritmoDeConsenso(Long coleccionId, String tipoAlgoritmo) {
        ModificarAlgoritmoDTO dto = new ModificarAlgoritmoDTO(tipoAlgoritmo);
        this.webClient.put().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId + "/algoritmo")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<SolicitudEliminacionDTO> obtenerSolicitudesDeEliminacionAdmin() {
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/solicitudes-eliminacion")
                .retrieve().bodyToFlux(SolicitudEliminacionDTO.class).collectList().block();
    }

    public void procesarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones/" + coleccionId + "/fuentes/" + fuenteId + "/procesar")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void aprobarSolicitud(Long solicitudId) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/solicitudes/" + solicitudId + "/aprobar")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void rechazarSolicitud(Long solicitudId) {
        this.webClient.post().uri(metamapaServiceUrl + "/admin/solicitudes/" + solicitudId + "/rechazar")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ResumenDashboardDTO obtenerResumenDashboard() {
        return this.webClient.get().uri(metamapaServiceUrl + "/admin/resumen-dashboard")
                .retrieve().bodyToMono(ResumenDashboardDTO.class).block();
    }

    public ColeccionDTO crearColeccion(ColeccionDTO dto) {
        return this.webClient.post().uri(metamapaServiceUrl + "/admin/colecciones")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ColeccionDTO.class)
                .block();
    }

    public void eliminarColeccion(Long coleccionId) {
        this.webClient.delete()
               .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ColeccionDTO obtenerColeccionPorId(Long coleccionId) {
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .retrieve()
                .bodyToMono(ColeccionDTO.class)
                .block();
    }

    public void modificarColeccion(Long coleccionId, ColeccionDTO dto) {
        this.webClient.put()
                .uri(metamapaServiceUrl + "/admin/colecciones/{id}", coleccionId)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void crearFuenteDataset(FuenteDTO dto) {
        this.webClient.post()
                .uri(metamapaServiceUrl + "/admin/fuentes")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<FuenteDTO> obtenerTodasLasFuentes() {
        return this.webClient.get()
                .uri(metamapaServiceUrl + "/admin/fuentes")
                .retrieve()
                .bodyToFlux(FuenteDTO.class)
                .collectList()
                .block();
    }

    public void eliminarFuente(Long fuenteId) {
        this.webClient.delete()
                .uri(metamapaServiceUrl + "/admin/fuentes/{id}", fuenteId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}