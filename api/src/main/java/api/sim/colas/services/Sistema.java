package api.sim.colas.services;

import api.sim.colas.dtos.VectorEstado;
import api.sim.colas.objetos.Peluquero;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Sistema {

    private float tiempoLlegadaMin;

    private float tiempoLlegadaMax;

    private List<Peluquero> peluqueros;

    private float[] probabilidadesAtencion;  // Lista de probabilidades para determinar qué peluquero atiende

    private VectorEstado vectorEstado;

    public void simularUnaFila() {
    }
}
