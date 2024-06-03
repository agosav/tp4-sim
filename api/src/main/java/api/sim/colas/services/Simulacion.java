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

    // Tiempo mínimo de llegada de los clientes
    private float tiempoLlegadaMin;

    // Tiempo  máximo de llegada de los clientes
    private float tiempoLlegadaMax;

    // Lista con las probabilidades acumuladas para determinar el peluquero de un cliente
    private float[] probabilidadesAtencion;

    // Próxima id de cliente disponible
    private int nextIdCliente;

    // ------------------------------------------------------------------------------------
    // ------------ Funciones para la generación y corrida de la simulación ---------------
    // ------------------------------------------------------------------------------------

    /**
     * Método encargado de inicializar toda la simulación y correrla.
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return lista con todos los vectores estado que se van a mostrar
     */
    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {
        VectorEstado vectorEstado = inicializar(dto);
        List<VectorEstado> tabla = new ArrayList<>();

        for (int iteracion = 0; iteracion < dto.getN(); iteracion++) {
            tabla.add(vectorEstado);
            vectorEstado = simularUnaFila(vectorEstado);
        }

        return tabla;

    }

    /**
     * Método encargado de setear uno por uno todos los parámetros y de crear el primer vector estado.
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return primer vector estado de toda la simulación
     */
    private VectorEstado inicializar(ParametrosDto dto) {
        this.nextIdCliente = 0;
        this.tiempoLlegadaMin = dto.getTiempoLlegadaMin();
        this.tiempoLlegadaMax = dto.getTiempoLlegadaMax();
        this.probabilidadesAtencion = Auxiliar.calcularProbabilidadesAcumuladas(dto);

        float random = (float) Math.random();
        float tiempoEntreLlegadas = tiempoLlegadaMin + random * (tiempoLlegadaMax - tiempoLlegadaMin);

        return VectorEstado.builder()
                .peluqueros(Auxiliar.inicializarPeluqueros(dto))
                .random1(random)
                .tiempoEntreLlegadas(tiempoEntreLlegadas)
                .proximaLlegada(tiempoEntreLlegadas)
                .build();
    }

    /**
     * Método encargado de simular una fila nueva
     * @param vectorAnterior: vector estado de la fila anterior
     * @return vector estado nuevo
     */
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
            finalizarAtencion(vectorProximo);
        }

        if (evento == Evento.INICIALIZACION) {
            iniciarNuevoDia(vectorProximo);
        }

        vectorProximo.actualizarVariablesEstadisticas();

        return vectorProximo;
    }

    // ------------------------------------------------------------------------------------
    // ---------------------- Funciones encargadas de cada evento -------------------------
    // ------------------------------------------------------------------------------------

    /**
     * Método encargado de toda la funcionalidad para el evento LLEGADA_CLIENTE.
     * @param vectorAnterior: vector estado de la fila anterior
     * @param vectorProximo: vector estado nuevo.
     * @return
     */
    private VectorEstado receptarCliente(VectorEstado vectorAnterior, VectorEstado vectorProximo) {
        // Creamos el nuevo objeto cliente
        this.nextIdCliente++;
        Cliente cliente = Cliente.builder().id(nextIdCliente).llegada(vectorProximo.getRelojDia()).build();
        vectorProximo.actualizarStringEvento("Llegada del cliente " + cliente.getId());

        // Calculamos la próxima llegada de un cliente
        vectorProximo.calcularProximaLlegada(tiempoLlegadaMin, tiempoLlegadaMax);

        // Determinamos quién va a atender a este cliente
        Peluquero peluquero = vectorProximo.determinarQuienLoAtiende(probabilidadesAtencion);
        cliente.setPeluquero(IdPeluqueroDto.fromPeluquero(peluquero));

        /*
        Si el peluquero está ocupado:
            - Sumamos 1 a su cola
            - Ponemos al cliente en estado ESPERANDO_ATENCION
            - Duplicamos el valor que tenía el peluquero en finAtencion
        */
        Float finAtencion;
        if (peluquero.estaOcupado()) {
            peluquero.sumarClienteACola();
            cliente.esperar();
            finAtencion = vectorAnterior.obtenerFinAtencionfromPeluquero(peluquero);


        /*
        Si el peluquero está libre:
            - Ponemos al peluquero en estado OCUPADO
            - Ponemos al cliente en estado SIENDO_ATENDIDO
            - Calculamos el tiempo de fin de atención
        */
        } else {
            finAtencion = vectorProximo.determinarFinAtencion(peluquero);
            peluquero.atender();
            cliente.serAtendido();
        }

        // Actualizamos vector estado
        vectorProximo.actualizarPeluquero(peluquero, finAtencion);
        vectorProximo.agregarCliente(cliente);

        return vectorProximo;
    }

    /**
     * Método encargado de toda la funcionalidad para el evento FIN_ATENCION.
     * @param vectorProximo: vector estado nuevo
     * @return
     */
    private VectorEstado finalizarAtencion(VectorEstado vectorProximo) {
        // Buscamos al peluquero y al cliente involucrados en este fin de atención
        Peluquero peluquero = vectorProximo.determinarQuePeluqueroFinalizoAtencion();
        Cliente cliente = vectorProximo.determinarClienteRecienAtendido(peluquero);
        vectorProximo.actualizarStringEvento("Fin atención del cliente " + cliente.getId() + " (" + peluquero.getNombre() + ")");

        /*
        Si el peluquero tiene cola:
            - Dejamos al peluquero en estado ocupado
            - Buscamos al primer cliente en la cola y lo ponemos en estado SIENDO_ATENDIDO
            - Calculamos el tiempo de fin de atención
        */
        // Si el peluquero tiene cola: ponemos en estado Siendo Atendido al próximo cliente
        Float finAtencion = null;
        if (peluquero.tieneCola()) {
            Cliente siguienteCliente = vectorProximo.determinarProximoClienteEnCola(peluquero);
            finAtencion = vectorProximo.determinarFinAtencion(peluquero);
            siguienteCliente.serAtendido();
            vectorProximo.actualizarAcumulador(siguienteCliente);
            vectorProximo.actualizarCliente(siguienteCliente);
        }

        // Actualizamos los atributos del peluquero
        peluquero.terminarAtencion();

        // Actualizamos vector estado
        vectorProximo.cobrarAtencion(peluquero);
        vectorProximo.quitarCliente(cliente);
        vectorProximo.actualizarPeluquero(peluquero, finAtencion);

        return vectorProximo;
    }

    public VectorEstado iniciarNuevoDia(VectorEstado vectorProximo) {
        int diaActual = vectorProximo.getDia();
        vectorProximo.actualizarStringEvento("Inicialización del día " + diaActual);

        return vectorProximo;
    }
}
