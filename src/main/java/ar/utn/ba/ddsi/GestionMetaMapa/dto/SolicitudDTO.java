package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SolicitudDTO {
    private Long id;
    private String motivo;
    private Long hechoId;
    private Long idContribuyente;
    private Long idAdministrador;
    private LocalDateTime fechaCreacion;
    private String userRole; // Nuevo campo

    public static SolicitudDTO toDTO(Long id, String motivo, Long hechoId, Long idContribuyente, Long idAdministrador, String userRole) {
        return SolicitudDTO
                .builder()
                .id(id)
                .motivo(motivo)
                .hechoId(hechoId)
                .idContribuyente(idContribuyente)
                .idAdministrador(idAdministrador)
                .userRole(userRole) // Asignar el nuevo campo
                .build();
    }
}