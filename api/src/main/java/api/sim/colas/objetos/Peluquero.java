package api.sim.colas.objetos;

import api.sim.colas.estados.EstadoPeluquero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Peluquero {

    private int id;

    private String nombre;

    @Builder.Default
    private EstadoPeluquero estado = EstadoPeluquero.LIBRE;

    @Builder.Default
    private int cola = 0;

    private int tarifa;

    // Parámetros para la distribución uniforme para el tiempo de atención
    private float tiempoAtencionMin;  // a

    private float tiempoAtencionMax;  // b

    public void atender() {
        this.estado = EstadoPeluquero.OCUPADO;
        this.cola = Math.max(0, cola - 1);
    }

    public void desocuparse() {
        this.estado = EstadoPeluquero.LIBRE;
    }

}
