package api.sim.colas.objetos;

import api.sim.colas.estados.EstadoCliente;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cliente {

    private int id;

    private EstadoCliente estado;

    private float acumuladorTiempoEspera;

    private Peluquero peluquero;

    public void serAtendido() {
        this.estado = EstadoCliente.SIENDO_ATENDIDO;
    }

    public void esperar() {
        this.estado = EstadoCliente.ESPERANDO_ATENCION;
    }

}
