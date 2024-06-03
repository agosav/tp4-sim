package api.sim.colas.services;

import api.sim.colas.dtos.IdPeluqueroDto;
import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.dtos.VectorEstado;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;
import api.sim.colas.utils.Auxiliar;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Simulacion {

    private float tiempoLlegadaMin;

    private float tiempoLlegadaMax;

    private List<Peluquero> peluqueros;

    private float[] probabilidadesAtencion;

    private int nextIdCliente = 0;

    // ------------------------------------------------------------------------------------
    // ------------ Funciones para la generación y corrida de la simulación ---------------
    // ------------------------------------------------------------------------------------

    // Este método inicializa la simulación y la corre
    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {
        VectorEstado vectorEstado = inicializar(dto);
        List<VectorEstado> tabla = new ArrayList<>();

        for (int iteracion = 0; iteracion < dto.getN(); iteracion++) {
            tabla.add(vectorEstado);
            vectorEstado = simularUnaFila(vectorEstado);
        }

        return tabla;

    }

    // Este método inicializa el sistema y setea todos los parámetros del usuario
    private VectorEstado inicializar(ParametrosDto dto) {
        this.tiempoLlegadaMin = dto.getTiempoLlegadaMin();
        this.tiempoLlegadaMax = dto.getTiempoLlegadaMax();
        this.peluqueros = Auxiliar.inicializarPeluqueros(dto);
        this.probabilidadesAtencion = Auxiliar.calcularProbabilidadesAcumuladas(dto);

        float random = (float) Math.random();
        float tiempoEntreLlegadas = tiempoLlegadaMin + random * (tiempoLlegadaMax - tiempoLlegadaMin);

        return VectorEstado.builder()
                .peluqueros(peluqueros)
                .random1(random)
                .tiempoEntreLlegadas(tiempoEntreLlegadas)
                .proximaLlegada(tiempoEntreLlegadas)
                .build();
    }

    // Este método se encarga de simular 1 fila.
    private VectorEstado simularUnaFila(VectorEstado vectorAnterior) {
        VectorEstado vectorProximo = VectorEstado.builder().build();

        // Este método determina el evento (llegada cliente o fin simulación)
        Evento evento = vectorProximo.determinarEvento(vectorAnterior);
        vectorProximo.duplicarVector(vectorAnterior, evento, tiempoLlegadaMin, tiempoLlegadaMax);

        // Acá llamamos un método distinto según el evento que corresponda a la fila actual
        if (evento == Evento.LLEGADA_CLIENTE) {
            receptarCliente(vectorAnterior, vectorProximo);
        }

        if (evento == Evento.FIN_ATENCION) {
            finalizarAtencion(vectorAnterior, vectorProximo);
        }

        return vectorProximo;
    }

    // ------------------------------------------------------------------------------------
    // ---------------------- Funciones encargadas de cada evento -------------------------
    // ------------------------------------------------------------------------------------

    private VectorEstado receptarCliente(VectorEstado vectorAnterior, VectorEstado vectorProximo) {
        // Creamos objeto cliente
        this.nextIdCliente++;
        Cliente cliente = Cliente.builder().id(nextIdCliente).build();

        // Calculamos el valor de la próxima llegada de un cliente
        vectorProximo.calcularProximaLlegada(tiempoLlegadaMin, tiempoLlegadaMax);

        // Determinamos quién va a atender a este cliente
        Peluquero peluquero = vectorProximo.determinarQuienLoAtiende(probabilidadesAtencion);
        cliente.setPeluquero(IdPeluqueroDto.fromPeluquero(peluquero));

        // Si el peluquero está ocupado,  sumamos 1 a su cola y ponemos al cliente en estado Esperando Atención
        Float finAtencion;
        if (peluquero.estaOcupado()) {
            peluquero.sumarClienteACola();
            cliente.esperar();
            finAtencion = vectorAnterior.obtenerFinAtencionfromPeluquero(peluquero);

        //Si el peluquero está libre, ponemos al peluquero en estado Ocupado y al cliente en estado Siendo atendido
        } else {
            finAtencion = vectorProximo.determinarFinAtencion(peluquero);
            peluquero.atender();
            cliente.serAtendido();
        }

        vectorProximo.actualizarPeluquero(peluquero, finAtencion);
        vectorProximo.actualizarStringEvento("Llegada del cliente " + cliente.getId());
        vectorProximo.agregarCliente(cliente);

        return vectorProximo;
    }

    private VectorEstado finalizarAtencion(VectorEstado vectorAnterior, VectorEstado vectorProximo) {
        // Buscamos al peluquero y al cliente involucrados en el Fin de atención
        Peluquero peluquero = vectorProximo.determinarQuePeluqueroFinalizoAtencion();
        Cliente cliente = vectorProximo.determinarClienteRecienAtendido(peluquero);

        // Si el peluquero tiene cola, ponemos en estado Siendo Atendido al próximo cliente
        Float finAtencion = null;
        if (peluquero.tieneCola()) {
            Cliente siguienteCliente = vectorProximo.determinarProximoClienteEnCola(peluquero);
            finAtencion = vectorProximo.determinarFinAtencion(peluquero);
            siguienteCliente.serAtendido();
            vectorProximo.actualizarCliente(siguienteCliente);
        }

        peluquero.terminarAtencion();

        vectorProximo.actualizarStringEvento("Fin atención del cliente " + cliente.getId() + " (" + peluquero.getNombre() + ")");
        vectorProximo.quitarCliente(cliente);
        vectorProximo.actualizarPeluquero(peluquero, finAtencion);

        return vectorProximo;
    }
}
