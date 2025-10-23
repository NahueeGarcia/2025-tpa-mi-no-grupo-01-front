package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificarAlgoritmoDTO {
    private String tipo; // El tipo de algoritmo (ej. "ABSOLUTO", "MAYORIA_SIMPLE", "MULTIPLES_MENCIONES")
 }
