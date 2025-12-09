package ar.utn.ba.ddsi.GestionMetaMapa.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HechoEdicionDTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String latitud;
    private String longitud;
    private LocalDateTime fecAcontecimiento;
}