package api.sim.colas.objetos;

import api.sim.colas.enums.EstadoPeluquero;
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

    public boolean estaLibre() {
        return estado == EstadoPeluquero.LIBRE;
    }

    public boolean estaOcupado() {
        return estado == EstadoPeluquero.OCUPADO;
    }

    public boolean tieneCola() {
        return cola != 0;
    }

    public void atender() {
        this.estado = EstadoPeluquero.OCUPADO;
        this.cola = Math.max(0, cola - 1);
    }

    public void terminarAtencion() {
        if (tieneCola()) {
            this.cola--;
        } else {
            this.estado = EstadoPeluquero.LIBRE;
        }
    }

    public void sumarClienteACola() {
        this.cola++;
    }

}
