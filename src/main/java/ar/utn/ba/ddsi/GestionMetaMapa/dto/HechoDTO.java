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
public class HechoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String latitud;
    private String longitud;
    private LocalDateTime fecAcontecimiento;
    private LocalDateTime fecCarga;

    public static HechoDTO toDTO(Long id, String titulo, String descripcion, String categoria, String latitud, String longitud) {
        return HechoDTO
                .builder()
                .id(id)
                .titulo(titulo)
                .descripcion(descripcion)
                .categoria(categoria)
                .latitud(latitud)
                .longitud(longitud)
                .fecAcontecimiento(LocalDateTime.now())
                .fecCarga(LocalDateTime.now())
                .build();
    }
}
