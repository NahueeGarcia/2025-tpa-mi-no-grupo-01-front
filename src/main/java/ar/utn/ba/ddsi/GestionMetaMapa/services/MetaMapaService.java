package ar.utn.ba.ddsi.GestionMetaMapa.services;

import ar.utn.ba.ddsi.GestionMetaMapa.dto.ColeccionDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.HechoDTO;
import ar.utn.ba.ddsi.GestionMetaMapa.dto.SolicitudDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaMapaService {
    @Autowired
    private GestionMetaMapaApiService gestionMetaMapaApiService;

    public List<HechoDTO> obtenerTodosLosHechos(){
        return gestionMetaMapaApiService.obtenerHechos();
    }
    public List<ColeccionDTO> obtenerTodasLasColecciones(){
        return gestionMetaMapaApiService.obtenerColecciones();
    }
    public List<HechoDTO> obtenerHechosPorColeccion(Long id, String navegacion){
        return gestionMetaMapaApiService.obtenerHechosPorColeccion(id, navegacion);
    }
    public void crearSolicitudEliminacion(SolicitudDTO solicitud){
        gestionMetaMapaApiService.crearSolicitud(solicitud);
    }
}
