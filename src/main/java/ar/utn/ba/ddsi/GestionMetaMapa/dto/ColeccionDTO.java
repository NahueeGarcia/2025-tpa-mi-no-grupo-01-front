package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ColeccionDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private List<HechoDTO> hechos;

    public static ColeccionDTO toDTO(Long id, String titulo, String descripcion, List<HechoDTO> hechos) {
        return ColeccionDTO
                .builder()
                .id(id)
                .titulo(titulo)
                .descripcion(descripcion)
                .hechos(hechos)
                .build();
    }
}