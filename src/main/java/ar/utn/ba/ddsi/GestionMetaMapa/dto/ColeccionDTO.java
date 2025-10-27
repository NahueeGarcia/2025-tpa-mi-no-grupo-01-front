package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColeccionDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoAlgoritmo;
    private List<FuenteDTO> fuentes; // Usamos el FuenteDTO que ya existe en el frontend


}