package api.sim.colas.dtos;


import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorEstado {

    @Builder.Default
    private EventoDto evento = EventoDto.builder().build();

    private float relojTotal;

    private float relojDelDia;

    private Float random1;

    private Float tiempoEntreLlegadas;

    private float proximaLlegada;

    private Float random2;

    private String nombreQuienAtiende;

    private Float random3;

    private Float tiempoAtencion;

    private List<PeluqueroDto> peluqueros;

    private float acumuladorCostos;

    private float acumuladorGanancias;

    private int horaDelDia;

    private int contadorDias;

    private float promedioRecaudacionDiaria;

    @Builder.Default
    private List<Cliente> listaClientes = new ArrayList<>();

}