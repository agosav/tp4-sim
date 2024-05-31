package api.sim.colas.services;

import api.sim.colas.dtos.PeluqueroDto;
import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.dtos.VectorEstado;
import api.sim.colas.objetos.Peluquero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@org.springframework.stereotype.Service
public class Simulacion {

    private Sistema sistema;

    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {

        // Calcular la primera llegada
        Random generador = new Random();
        float random1 = generador.nextFloat();
        float tiempoEntreLlegadas = dto.getTiempoLlegadaMin() + random1 * (dto.getTiempoLlegadaMax() - dto.getTiempoLlegadaMin());

        // Crear peluqueros
        Peluquero aprendiz = Peluquero.builder()
                .id(1)
                .nombre("Aprendiz")
                .tarifa(1800)
                .tiempoAtencionMin(dto.getTiempoAtencionMinAprendiz())
                .tiempoAtencionMax(dto.getTiempoAtencionMaxAprendiz())
                .build();

        Peluquero veteranoA = Peluquero.builder()
                .id(2)
                .nombre("Veterano A")
                .tarifa(3500)
                .tiempoAtencionMin(dto.getTiempoAtencionMinVeteranoA())
                .tiempoAtencionMax(dto.getTiempoAtencionMaxVeteranoA())
                .build();

        Peluquero veteranoB = Peluquero.builder()
                .id(3)
                .nombre("Veterano B")
                .tarifa(3500)
                .tiempoAtencionMin(dto.getTiempoAtencionMinVeteranoB())
                .tiempoAtencionMax(dto.getTiempoAtencionMaxVeteranoB())
                .build();

        // Inicializar vector estado
        VectorEstado vectorEstado = VectorEstado.builder()
                .aprendiz(PeluqueroDto.fromPeluquero(aprendiz))
                .veteranoA(PeluqueroDto.fromPeluquero(veteranoA))
                .veteranoB(PeluqueroDto.fromPeluquero(veteranoB))
                .random1(random1)
                .tiempoEntreLlegadas(tiempoEntreLlegadas)
                .proximaLlegada(tiempoEntreLlegadas)
                .build();

        // Inicializar sistema con todos los parámetros del usuario
        this.sistema = Sistema.builder()
                .tiempoLlegadaMin(dto.getTiempoLlegadaMin())
                .tiempoLlegadaMax(dto.getTiempoLlegadaMax())
                .peluqueros(List.of(aprendiz, veteranoA, veteranoB))
                .probabilidadesAtencion(new float[]{dto.getPorcentajeAprendiz(), dto.getPorcentajeVeteranoA(), dto.getPorcentajeVeteranoB()})
                .vectorEstado(vectorEstado)
                .build();

        // Realizar n simulaciones
        int n = dto.getN();  // cantidad total de días a simular
        int i = dto.getI();  // cantidad de iteraciones que se van a mostrar en la tabla
        int j = dto.getJ();  // hora de la primera iteración que se va a mostrar en la tabla

        List<VectorEstado> tabla = new ArrayList<>();

        for (int iteracion = 1; iteracion < n + 1; iteracion++) {
            sistema.simularUnaFila();

            float reloj = sistema.getVectorEstado().getRelojTotal();

            if (i <= (reloj / 60) && j >= 0 || iteracion == n) {
                j--;
                tabla.add(sistema.getVectorEstado());
            }
        }

        return tabla;
    }
}
