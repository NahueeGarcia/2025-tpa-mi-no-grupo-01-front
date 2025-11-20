package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEliminacionDTO {
    private Long id;
    private Long idHecho;
    private String motivo;
    private LocalDateTime fechaCreacion;
    private String estado; // Ej. "PENDIENTE", "APROBADA", "RECHAZADA"
    private String nombreUsuario; // Opcional, si se quiere mostrar quién la solicitó
}