package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDashboardDTO {
    private long totalColecciones;
    private long totalHechos;
    private long totalFuentes;
    private long totalSolicitudesPendientes;
}
