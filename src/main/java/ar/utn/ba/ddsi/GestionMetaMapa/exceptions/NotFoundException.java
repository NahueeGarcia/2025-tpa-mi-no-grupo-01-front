package ar.utn.ba.ddsi.GestionMetaMapa.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String entidad, String id) {
        super("No se ha encontrado " + entidad + " de id " + id);
    }
}