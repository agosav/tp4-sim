package api.sim.colas.dtos;


import api.sim.colas.objetos.Cliente;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class VectorEstado {

    @Builder.Default
    private String evento = "Inicialización";

    private float relojTotal;

    private float relojDelDia;

    private Float random1;

    private Float tiempoEntreLlegadas;

    private float proximaLlegada;

    private Float random2;

    private String nombreQuienAtiende;

    private Float random3;

    private Float tiempoAtencion;

    private float finAtencion1;

    private float finAtencion2;

    private float finAtencion3;

    private PeluqueroDto aprendiz;

    private PeluqueroDto veteranoA;

    private PeluqueroDto veteranoB;

    private float acumuladorCostos;

    private float acumuladorGanancias;

    private int horaDelDía;

    private int contadorDias;

    private float promedioRecaudacionDiaria;

    private List<Cliente> listaClientes;

}