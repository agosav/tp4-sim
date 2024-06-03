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
     *
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return lista con todos los vectores estado que se van a mostrar
     */
    public List<VectorEstado> realizarSimulacion(ParametrosDto dto) {
        VectorEstado vectorEstado = inicializar(dto);
        List<VectorEstado> tabla = new ArrayList<>();
        int n = dto.getN();
        int i = dto.getI();
        int j = dto.getJ();

        for (int iteracion = 1; iteracion < n + 1; iteracion++) {
            int hora = vectorEstado.getHoraTotal();
            if (j <= hora && i >= 0 || iteracion == n) {
                tabla.add(vectorEstado);
                vectorEstado = simularUnaFila(vectorEstado);
                i--;
            }

            vectorEstado = simularUnaFila(vectorEstado);
        }

        return tabla;

    }

    /**
     * Método encargado de setear uno por uno todos los parámetros y de crear el primer vector estado.
     *
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return primer vector estado de toda la simulación
     */
    private VectorEstado inicializar(ParametrosDto dto) {
        if (dto.getI() > 100000) {
            throw new IllegalArgumentException("La cantidad de iteraciones a mostrar debe ser menor a 100.000");
        }

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
     *
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
            receptarCliente(vectorProximo);
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
     *
     * @param vector: vector estado nuevo
     */
    private void receptarCliente(VectorEstado vector) {
        // Creamos el nuevo objeto cliente
        this.nextIdCliente++;
        Cliente cliente = Cliente.builder().id(nextIdCliente).llegada(vector.getRelojDia()).build();
        vector.actualizarStringEvento("Llegada del cliente " + cliente.getId());

        // Calculamos la próxima llegada de un cliente
        vector.calcularProximaLlegada(tiempoLlegadaMin, tiempoLlegadaMax);

        // Determinamos quién va a atender a este cliente
        Peluquero peluquero = vector.determinarQuienLoAtiende(probabilidadesAtencion);
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
            finAtencion = vector.obtenerFinAtencionfromPeluquero(peluquero);

        /*
        Si el peluquero está libre:
            - Ponemos al peluquero en estado OCUPADO
            - Ponemos al cliente en estado SIENDO_ATENDIDO
            - Calculamos el tiempo de fin de atención
         */
        } else {
            finAtencion = vector.determinarFinAtencion(peluquero);
            peluquero.atender();
            cliente.serAtendido();
        }

        // Actualizamos vector estado
        vector.actualizarPeluquero(peluquero, finAtencion);
        vector.agregarCliente(cliente);
    }

    /**
     * Método encargado de toda la funcionalidad para el evento FIN_ATENCION.
     *
     * @param vector: vector estado nuevo
     */
    private void finalizarAtencion(VectorEstado vector) {
        // Buscamos al peluquero y al cliente involucrados en este fin de atención
        Peluquero peluquero = vector.determinarQuePeluqueroFinalizoAtencion();
        Cliente cliente = vector.determinarClienteRecienAtendido(peluquero);
        vector.actualizarStringEvento("Fin atención del cliente " + cliente.getId() + " (" + peluquero.getNombre() + ")");

        /*
        Si el peluquero tiene cola:
            - Dejamos al peluquero en estado ocupado
            - Buscamos al primer cliente en la cola y lo ponemos en estado SIENDO_ATENDIDO
            - Calculamos el tiempo de fin de atención
         */
        // Si el peluquero tiene cola: ponemos en estado Siendo Atendido al próximo cliente
        Float finAtencion = null;
        if (peluquero.tieneCola()) {
            Cliente siguienteCliente = vector.determinarProximoClienteEnCola(peluquero);
            finAtencion = vector.determinarFinAtencion(peluquero);
            siguienteCliente.serAtendido();
            vector.actualizarAcumulador(siguienteCliente);
            vector.actualizarCliente(siguienteCliente);
        }

        // Actualizamos los atributos del peluquero
        peluquero.terminarAtencion();

        // Actualizamos vector estado
        vector.cobrarAtencion(peluquero);
        vector.quitarCliente(cliente);
        vector.actualizarPeluquero(peluquero, finAtencion);
    }

    /**
     * Método encargado de toda la funcionalidad para el evento INICIALIZACION.
     *
     * @param vector: vector estado nuevo
     */
    public void iniciarNuevoDia(VectorEstado vector) {
        int diaActual = vector.getDia();
        vector.actualizarStringEvento("Inicialización del día " + diaActual);
    }
}
