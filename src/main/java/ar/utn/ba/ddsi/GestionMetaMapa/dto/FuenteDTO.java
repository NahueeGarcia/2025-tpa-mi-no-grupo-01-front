 package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuenteDTO {
    private Long id;
    private String nombre;
    private String tipo; // Puede ser "ESTATICA", "DINAMICA", "PROXY"
    private String path;
    private Boolean esRegistrado;
}