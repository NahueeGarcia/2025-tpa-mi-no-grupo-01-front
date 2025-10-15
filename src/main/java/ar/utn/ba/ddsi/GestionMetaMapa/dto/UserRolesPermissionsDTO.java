package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Data;

@Data
public class UserRolesPermissionsDTO {
    private String username;
    private String rol; // Lo recibimos como un simple String
    // Ignoramos la lista de permisos por ahora para simplificar
}