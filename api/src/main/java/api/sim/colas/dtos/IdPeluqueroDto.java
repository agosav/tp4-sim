package api.sim.colas.dtos;

import api.sim.colas.objetos.Peluquero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdPeluqueroDto {

    private int id;

    private String nombre;

    public static IdPeluqueroDto fromPeluquero(Peluquero peluquero) {
        return IdPeluqueroDto.builder()
                .id(peluquero.getId())
                .nombre(peluquero.getNombre())
                .build();
    }

}
