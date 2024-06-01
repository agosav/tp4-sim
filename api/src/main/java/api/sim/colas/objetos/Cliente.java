package api.sim.colas.objetos;

import api.sim.colas.dtos.IdPeluqueroDto;
import api.sim.colas.dtos.PeluqueroDto;
import api.sim.colas.enums.EstadoCliente;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cliente {

    private int id;

    @Builder.Default
    private EstadoCliente estado = EstadoCliente.INICIALIZADO;

    private float acumuladorTiempoEspera;

    private IdPeluqueroDto peluquero;

    public void serAtendido() {
        this.estado = EstadoCliente.SIENDO_ATENDIDO;
    }

    public void esperar() {
        this.estado = EstadoCliente.ESPERANDO_ATENCION;
    }

}
