package api.sim.colas.services;

import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.dtos.PeluqueroDto;
import api.sim.colas.dtos.VectorEstado;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Peluquero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class Simulacion {

    private static final Logger logger = LoggerFactory.getLogger(Simulacion.class);

    private float tiempoLlegadaMin;

    private float tiempoLlegadaMax;

    private List<Peluquero> peluqueros;

    private float[] probabilidadesAtencion;

    private VectorEstado vectorEstado;

    private VectorEstado vectorEstadoProximo;

    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {

        this.tiempoLlegadaMin = dto.getTiempoLlegadaMin();
        this.tiempoLlegadaMax = dto.getTiempoLlegadaMax();
        this.peluqueros = inicializarPeluqueros(dto);
        this.probabilidadesAtencion = calcularProbabilidadesAcumuladas(dto);
        this.vectorEstadoProximo = new VectorEstado();

        float random1 = (float) Math.random();
        float tiempoEntreLlegadas = tiempoLlegadaMin + random1 * (tiempoLlegadaMax - tiempoLlegadaMin);

        this.vectorEstado = VectorEstado.builder()
                .peluqueros(peluqueros.stream().map(PeluqueroDto::fromPeluquero).toList())
                .random1(random1)
                .tiempoEntreLlegadas(tiempoEntreLlegadas)
                .proximaLlegada(tiempoEntreLlegadas)
                .build();

        return simularNFilas(dto.getN(), dto.getI(), dto.getJ());
    }

    private List<Peluquero> inicializarPeluqueros(ParametrosDto dto) {
        Peluquero aprendiz = Peluquero.builder()
                .id(0)
                .nombre("Aprendiz")
                .tarifa(1800)
                .tiempoAtencionMin(dto.getTiempoAtencionMinAprendiz())
                .tiempoAtencionMax(dto.getTiempoAtencionMaxAprendiz())
                .build();

        Peluquero veteranoA = Peluquero.builder()
                .id(1)
                .nombre("Veterano A")
                .tarifa(3500)
                .tiempoAtencionMin(dto.getTiempoAtencionMinVeteranoA())
                .tiempoAtencionMax(dto.getTiempoAtencionMaxVeteranoA())
                .build();

        Peluquero veteranoB = Peluquero.builder()
                .id(2)
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
            tabla.add(vectorEstado);
            simularUnaFila();
        }

        return tabla;
    }

    private void simularUnaFila() {
        determinarReloj();

        if (vectorEstadoProximo.getEvento() == Evento.LLEGADA_CLIENTE) {
            receptarCliente();
        } else {
            finalizarAtencion();
        }

        this.vectorEstado = vectorEstadoProximo;
        this.vectorEstadoProximo = new VectorEstado();
    }

    private void duplicarProximaLlegada() {
        vectorEstadoProximo.setProximaLlegada(vectorEstado.getProximaLlegada());
    }

    private void determinarReloj() {
        float proximoReloj;
        float proximaLlegada = vectorEstado.getProximaLlegada();
        float finAtencionMin = vectorEstado.getPeluqueros().stream()
                .map(PeluqueroDto::getFinAtencion)
                .filter(Objects::nonNull)
                .min(Float::compare)
                .orElse(Float.MAX_VALUE);

        if (proximaLlegada < finAtencionMin) {
            proximoReloj = proximaLlegada;
            vectorEstadoProximo.setEvento(Evento.LLEGADA_CLIENTE);
        } else {
            proximoReloj = finAtencionMin;
            vectorEstadoProximo.setEvento(Evento.FIN_ATENCION);
        }

        vectorEstadoProximo.setRelojTotal(proximoReloj);
    }

    /*
     Mantiene:
         - finAtencion(i)
     Actualiza:
         - random1
         - tiempoEntreLlegadas
         - proximaLlegada

         - random2
         - nombreQuienAtiende

         Si el peluquero está ocupado, actualiza:
             - cola
         Si el peluquero está libre, actualiza:
             - random3
             - tiempoAtencion
             - finAtencion(i)
             - estado (del peluquero)
      */
    private void receptarCliente() {
        calcularProximaLlegada();

        Peluquero peluquero = determinarQuienLoAtiende();
        Float finAtencion = vectorEstado.getPeluqueros().get(peluquero.getId()).getFinAtencion();

        if (peluquero.estaOcupado()) {
            peluquero.sumarClienteACola();
        } else {
            finAtencion = determinarTiempoAtencion(peluquero);
            peluquero.atender();
        }

        actualizarPeluqueros(peluquero, finAtencion);
    }

    /*
    Mantiene:
        - proximaLlegada
        - finAtencion(i)
    Actualiza:
        - acumuladorGanancias
        Si el peluquero tiene cola, actualiza:
            - cola
            - random3
            - tiempoAtencion
            - finAtencion(i)
        Si el peluquero no tiene cola, actualiza:
            - finAtencion(i)
            - estado (del peluquero)
     */
    private void finalizarAtencion() {
        Float finAtencion;

        duplicarProximaLlegada();

        Peluquero peluquero = determinarQuienFinalizoAtencion();

        if (peluquero.tieneCola()) {
            finAtencion = determinarTiempoAtencion(peluquero);
        } else {
            finAtencion = null;
        }

        peluquero.terminarAtencion();

        actualizarPeluqueros(peluquero, finAtencion);
    }

    private void calcularProximaLlegada() {
        float reloj = vectorEstadoProximo.getRelojTotal();
        float random = (float) Math.random();
        float a = tiempoLlegadaMin;
        float b = tiempoLlegadaMax;

        float tiempoEntreLlegadas = a + random * (b - a);

        vectorEstadoProximo.setRandom1(random);
        vectorEstadoProximo.setTiempoEntreLlegadas(tiempoEntreLlegadas);
        vectorEstadoProximo.setProximaLlegada(reloj + tiempoEntreLlegadas);
    }

    private Peluquero determinarQuienLoAtiende() {
        float random = (float) Math.random();
        Peluquero peluquero = null;

        for (int i = 0; i < probabilidadesAtencion.length; i++) {
            if (random < probabilidadesAtencion[i]) {
                peluquero = peluqueros.get(i);
                break;
            }
        }

        vectorEstadoProximo.setRandom2(random);
        vectorEstadoProximo.setNombreQuienAtiende(peluquero.getNombre());

        return peluquero;
    }

    private float determinarTiempoAtencion(Peluquero peluquero) {
        float reloj = vectorEstadoProximo.getRelojTotal();
        float random = (float) Math.random();
        float a = peluquero.getTiempoAtencionMin();
        float b = peluquero.getTiempoAtencionMax();

        float tiempoAtencion = a + random * (b - a);
        float finAtencion = reloj + tiempoAtencion;

        vectorEstadoProximo.setRandom3(random);
        vectorEstadoProximo.setTiempoAtencion(tiempoAtencion);

        return finAtencion;
    }

    private Peluquero determinarQuienFinalizoAtencion() {
        float reloj = vectorEstadoProximo.getRelojTotal();

        for (int i = 0; i < vectorEstado.getPeluqueros().size(); i++) {
            Float finAtencion = vectorEstado.getPeluqueros().get(i).getFinAtencion();
            if (finAtencion != null && finAtencion == reloj) {
                return peluqueros.get(i);
            }
        }

        return null;
    }

    private void actualizarPeluqueros(Peluquero peluquero, Float finAtencion) {
        PeluqueroDto peluqueroDto = PeluqueroDto.builder()
                .nombre(peluquero.getNombre())
                .cola(peluquero.getCola())
                .estado(peluquero.getEstado())
                .finAtencion(finAtencion)
                .build();

        List<PeluqueroDto> peluquerosDuplicados = new ArrayList<>(vectorEstado.getPeluqueros());
        peluquerosDuplicados.set(peluquero.getId(), peluqueroDto);
        vectorEstadoProximo.setPeluqueros(peluquerosDuplicados);
    }
}
