package api.sim.colas.objetos;

import api.sim.colas.enums.EstadoPeluquero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Peluquero implements Cloneable {

    private int id;

    private String nombre;

    @Builder.Default
    private EstadoPeluquero estado = EstadoPeluquero.LIBRE;

    @Builder.Default
    private int cola = 0;

    private int tarifa;

    private Float finAtencion;

    // Parámetros para la distribución uniforme para el tiempo de atención
    private float tiempoAtencionMin;  // a

    private float tiempoAtencionMax;  // b

    public boolean estaOcupado() {
        return estado == EstadoPeluquero.OCUPADO;
    }

    public boolean tieneCola() {
        return cola != 0;
    }

    public void atender() {
        this.estado = EstadoPeluquero.OCUPADO;
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

    @Override
    public Peluquero clone() {
        try {
            return (Peluquero) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
