package api.sim.colas.dtos;


import api.sim.colas.estados.EstadoPeluquero;
import api.sim.colas.objetos.Peluquero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeluqueroDto {

    private String nombre;

    private EstadoPeluquero estado;

    private int cola;

    public static PeluqueroDto fromPeluquero(Peluquero peluquero) {
        return PeluqueroDto.builder()
                .nombre(peluquero.getNombre())
                .estado(peluquero.getEstado())
                .cola(peluquero.getCola())
                .build();
    }
}