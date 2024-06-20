package api.sim.colas.simulacion;

import api.sim.colas.dtos.IdPeluqueroDto;
import api.sim.colas.dtos.RequestDto;
import api.sim.colas.enums.EstadoCliente;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;
import api.sim.colas.utils.Auxiliar;
import api.sim.colas.utils.DistribucionUniforme;
import api.sim.colas.utils.RungeKutta;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class Gestor {

    // Tiempo mínimo de llegada de los clientes
    private float tiempoLlegadaMin;

    // Tiempo  máximo de llegada de los clientes
    private float tiempoLlegadaMax;

    // Lista con las probabilidades acumuladas para determinar el peluquero de un cliente
    private float[] probabilidadesAtencion;

    // Próxima id de cliente disponible
    private int nextIdCliente;

    // Runge Kutta
    private RungeKutta rungeKutta;

    // ------------------------------------------------------------------------------------
    // ------------ Funciones para la generación y corrida de la simulación ---------------
    // ------------------------------------------------------------------------------------

    /**
     * Método encargado de inicializar toda la simulación y correrla.
     *
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return lista con todos los vectores estado que se van a mostrar
     */
    public List<VectorEstado> realizarSimulacion(RequestDto dto) {
        VectorEstado vectorEstado = inicializar(dto);
        List<VectorEstado> tabla = new ArrayList<>();

        int cantidadDias = dto.getCantidadDias();  // Cantidad de días a simular en total
        int cantidadIteraciones = dto.getCantidadIteraciones();  // Cantidad de iteraciones a mostrar
        int diaDesde = dto.getDiaDesde();
        int horaDesde = dto.getHoraDesde();
        int contador = 0;  // Contador de iteraciones

        // Simulación
        while (true) {
            int dia = vectorEstado.getDia();
            int hora = vectorEstado.getHoraActual();
            boolean esLaUltimaFila = vectorEstado.esLaUltimaFila(cantidadDias, contador);

            // Agregar vector a la tabla que se va a mostrar
            if (diaDesde * 8 + horaDesde <= dia * 8 + hora && cantidadIteraciones >= 0 || esLaUltimaFila) {
                tabla.add(vectorEstado);
                cantidadIteraciones--;

                if (esLaUltimaFila) {
                    break;
                }
            }

            vectorEstado = simularUnaFila(vectorEstado);
            contador++;
        }

        return tabla;
    }

    /**
     * Método encargado de setear uno por uno todos los parámetros y de crear el primer vector estado.
     *
     * @param dto: Data Transfer Object para recibir todos los parámetros del usuario
     * @return primer vector estado de toda la simulación
     */
    private VectorEstado inicializar(RequestDto dto) {
        Auxiliar.validarParametrosDistribuciones(dto);

        this.nextIdCliente = 0;
        this.tiempoLlegadaMin = dto.getTiempoLlegadaMin();
        this.tiempoLlegadaMax = dto.getTiempoLlegadaMax();
        this.probabilidadesAtencion = Auxiliar.calcularProbabilidadesAcumuladas(dto);

        // Realizar una sola vez la tabla del runge kutta
        RungeKutta rk = RungeKutta.builder()
                .h(dto.getH())
                .x0(0)
                .C0(0)
                .primerNum(dto.getPrimerNum())
                .segundoNum(dto.getSegundoNum())
                .tercerNum(dto.getTercerNum())
                .complejidad(dto.getComplejidadMax())
                .build();

        rk.realizarTabla();

        this.rungeKutta = rk;

        float random = (float) Math.random();
        float tiempoEntreLlegadas = DistribucionUniforme.generar(random, tiempoLlegadaMin, tiempoLlegadaMax);

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
        switch (evento) {
            case LLEGADA_CLIENTE -> receptarCliente(vectorProximo);
            case FIN_ATENCION -> finalizarAtencion(vectorProximo);
            case INICIALIZACION -> iniciarNuevoDia(vectorProximo);
        }

        vectorProximo.actualizarVariablesEstadisticas(vectorProximo.getClientes());

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
        Cliente cliente = Cliente.builder().id(nextIdCliente).tiempoLlegada(vector.getRelojActual()).build();
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
            // Determinar tiempo atención para Veterano A
            if (peluquero.getId() == 1) {
                finAtencion = vector.determinarFinAtencionVeteranoA(peluquero, rungeKutta);

            // Determianr tiempo atención para Aprendiz o Veterano B
            } else {
                finAtencion = vector.determinarFinAtencionOtrosPeluqueros(peluquero);
            }
            peluquero.atender();
            cliente.serAtendido(vector.getRelojActual());
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
        Cliente cliente = vector.buscarCliente(peluquero, EstadoCliente.SIENDO_ATENDIDO);
        vector.actualizarStringEvento("Fin atención del cliente " + cliente.getId() + " (" + peluquero.getNombre() + ")");

        /*
        Si el peluquero tiene cola:
            - Dejamos al peluquero en estado ocupado
            - Buscamos al primer cliente en la cola y lo ponemos en estado SIENDO_ATENDIDO
            - Calculamos el tiempo de fin de atención
         */
        Float finAtencion = null;
        if (peluquero.tieneCola()) {
            Cliente siguienteCliente = vector.buscarCliente(peluquero, EstadoCliente.ESPERANDO_ATENCION);
            // Determinar tiempo atención para Veterano A
            if (peluquero.getId() == 1) {
                finAtencion = vector.determinarFinAtencionVeteranoA(peluquero, rungeKutta);

            // Determianr tiempo atención para Aprendiz o Veterano B
            } else {
                finAtencion = vector.determinarFinAtencionOtrosPeluqueros(peluquero);
            }
            siguienteCliente.serAtendido(vector.getRelojActual());
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
