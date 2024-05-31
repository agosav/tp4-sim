package api.sim.colas.services;

import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.dtos.PeluqueroDto;
import api.sim.colas.dtos.VectorEstado;
import api.sim.colas.objetos.Peluquero;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class Simulacion {

    private float tiempoLlegadaMin;

    private float tiempoLlegadaMax;

    private List<Peluquero> peluqueros;

    private float[] probabilidadesAtencion;  // Lista de probabilidades para determinar qué peluquero atiende

    private VectorEstado vectorEstado;

    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {

        // Inicializamos la simulación con los valores de los parámetros ingresados por el usuario
        this.tiempoLlegadaMin = dto.getTiempoLlegadaMin();
        this.tiempoLlegadaMax = dto.getTiempoLlegadaMax();
        this.peluqueros = inicializarPeluqueros(dto);
        this.probabilidadesAtencion = calcularProbabilidadesAcumuladas(dto);

        // Calculamos la primera llegada
        float random1 = (float) Math.random();
        float tiempoEntreLlegadas = tiempoLlegadaMin + random1 * (tiempoLlegadaMax - tiempoLlegadaMin);

        // Inicializamos vector estado
        this.vectorEstado = VectorEstado.builder()
                .aprendiz(PeluqueroDto.fromPeluquero(peluqueros.get(0)))
                .veteranoA(PeluqueroDto.fromPeluquero(peluqueros.get(1)))
                .veteranoB(PeluqueroDto.fromPeluquero(peluqueros.get(2)))
                .random1(random1)
                .tiempoEntreLlegadas(tiempoEntreLlegadas)
                .proximaLlegada(tiempoEntreLlegadas)
                .build();

        // Simulamos n filas y devolvemos tabla final
        return simularNFilas(dto.getN(), dto.getI(), dto.getJ());
    }

    private List<Peluquero> inicializarPeluqueros(ParametrosDto dto) {
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

        return List.of(aprendiz, veteranoA, veteranoB);
    }

    private float[] calcularProbabilidadesAcumuladas(ParametrosDto dto) {
        float aprendiz = dto.getPorcentajeAprendiz() / 100;
        float veteranoA = dto.getPorcentajeVeteranoA() / 100;
        float veteranoB = dto.getPorcentajeVeteranoB() / 100;

        float[] acumuladas = {
                aprendiz,
                aprendiz + veteranoA,
                aprendiz + veteranoA + veteranoB
        };

        if (acumuladas[2] != 1) {
            throw new IllegalArgumentException("La suma de las probabilidades de los peluqueros no da 100%");
        }

        return acumuladas;
    }

    private List<VectorEstado> simularNFilas(int n, int i, int j) {
        List<VectorEstado> tabla = new ArrayList<>();

        for (int iteracion = 1; iteracion < n + 1; iteracion++) {
            simularUnaFila();

            float reloj = vectorEstado.getRelojTotal();

            if (i <= (reloj / 60) && j >= 0 || iteracion == n) {
                j--;
                tabla.add(vectorEstado);
            }
        }

        return tabla;
    }

    public void simularUnaFila() {
    }

    /*
    private float calcularProximaLlegada() {
        float random1 = generador.nextFloat();
        float tiempoEntreLlegadas = tiempoLlegadaMin + random1 * (tiempoLlegadaMax - tiempoLlegadaMin);
        return tiempoEntreLlegadas;
    }
     */
}
