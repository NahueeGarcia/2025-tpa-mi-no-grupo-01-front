package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // Si usas Lombok, sino genera getters y setters
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Integer edad;
    private String pais;
}
