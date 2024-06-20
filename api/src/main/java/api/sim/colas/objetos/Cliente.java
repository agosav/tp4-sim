package api.sim.colas.objetos;

import api.sim.colas.dtos.IdPeluqueroDto;
import api.sim.colas.enums.EstadoCliente;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cliente implements Cloneable {

    private int id;

    @Builder.Default
    private EstadoCliente estado = EstadoCliente.INICIALIZADO;

    private float tiempoLlegada;

    private Float tiempoAtencion;

    private Float acumuladorTiempoEspera;

    @Builder.Default
    private boolean puedeRecibirRefresco = true;

    private IdPeluqueroDto peluquero;

    public void serAtendido(float reloj) {
        this.estado = EstadoCliente.SIENDO_ATENDIDO;
        this.tiempoAtencion = reloj;
        this.acumuladorTiempoEspera = Math.max(reloj - tiempoLlegada, 0);
        this.puedeRecibirRefresco = false;
    }

    public void esperar() {
        this.estado = EstadoCliente.ESPERANDO_ATENCION;
    }

    public void actualizarAcumulador(float reloj) {
        if (puedeRecibirRefresco) {
            this.acumuladorTiempoEspera = reloj - tiempoLlegada;
        }
    }

    public boolean puedeRecibirRefresco() {
        return puedeRecibirRefresco;
    }

    @Override
    public Cliente clone() {
        try {
            return (Cliente) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
