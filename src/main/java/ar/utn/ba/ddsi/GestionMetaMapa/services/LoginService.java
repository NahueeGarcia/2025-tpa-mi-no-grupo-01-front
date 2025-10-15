package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.UserRolesPermissionsDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
 import reactor.core.publisher.Mono;

import java.util.Map;

 @Service
public class LoginService {

    private final WebClient webClient;

    public LoginService() {
        // Este WebClient apunta al API Gateway en el puerto 8089
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8089/api/auth")
                .build();
    }

    public AuthResponseDTO autenticar(String username, String password) {
        // Crea el cuerpo de la petición con las credenciales
        Map<String, String> credentials = Map.of("username", username, "password", password);

        // Llama al endpoint POST /api/auth del Gateway
        Mono<AuthResponseDTO> mono = this.webClient.post()
                .uri("") // La URI ya está en la baseUrl
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON) // Especificamos que enviamos JSON
                .bodyValue(credentials) // Enviamos el Map como cuerpo JSON
                .retrieve()
                .bodyToMono(AuthResponseDTO.class);

        try {
            return mono.block();
        } catch (Exception e) {
            System.err.println("Error al autenticar: " + e.getMessage()); // Log para ver el error
            return null;
        }
    }
        public AuthResponseDTO autenticarComoVisualizador() {
            // Las credenciales deben coincidir con las del backend
            return autenticar("visualizador", "visualizador");
        }


    public UserRolesPermissionsDTO obtenerRolesYPermisos(String token) {
        Mono<UserRolesPermissionsDTO> mono = this.webClient.get()
                .uri("/user/roles-permisos") // SIN barras de escape
                 .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserRolesPermissionsDTO.class);

        try {
            return mono.block();
        } catch (Exception e) {
            System.err.println("Error al obtener roles: " + e.getMessage()); // SIN barras de escape
            return null;
        }
    }

}