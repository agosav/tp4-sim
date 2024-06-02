package api.sim.colas.dtos;

import api.sim.colas.enums.Evento;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventoDto {

    @Builder.Default
    private Evento evento = Evento.INICIALIZACION;

    @Builder.Default
    private String string = "Inicialización";

}
