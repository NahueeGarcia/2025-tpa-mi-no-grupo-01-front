package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesPermissionsDTO {
    private String username;
    private String rol; // Lo recibimos como un simple String
    private Long userId; // Nuevo campo
    // Ignoramos la lista de permisos por ahora para simplificar
}