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
    private String motivo;
    private Long idHecho;
    private String estado;
}