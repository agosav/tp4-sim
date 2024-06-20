package api.sim.colas.simulacion;

import api.sim.colas.enums.EstadoCliente;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;
import api.sim.colas.utils.DistribucionUniforme;
import api.sim.colas.utils.RungeKutta;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Builder
@Getter
public class VectorEstado {

    // Número de iteración
    private int iteracion;

    // String que especifica el evento simulado
    @Builder.Default
    private String evento = "Inicialización";

    // Acumulador de todos las minutos de la simulacion
    private float relojTotal;

    // Acumulador de todas las horas de la simulación
    private int horaTotal;

    // Reloj en minutos del día actual
    private float relojActual;

    // Reloj en horas del día actual
    // Si el relojDia está entre 0 y 59, la hora es 1.
    // Si el relojDia está entre 60 y 119, la hora es 2. Etc
    @Builder.Default
    private int horaActual = 1;

    // Contador de días simulados en toda la simulación
    @Builder.Default
    private int dia = 1;

    // Para determinar la próxima llegada
    private Float random1;
    private Float tiempoEntreLlegadas;
    private Float proximaLlegada;

    // Para determinar qué peluquero va a atender al cliente
    private Float random2;
    private String nombreQuienAtiende;

    // Para determinar tiempo de atención si es veterano A
    private Float random3;
    private Float complejidad;
    private Float rungeKutta;

    // Para determinar tiempo de atención si no es veterano A
    private Float random4;

    // Columna para mostrar el tiempo de atención (independientemente de cómo fue calculado)
    private Float tiempoAtencion;

    // Lista con los peluqueros
    @Builder.Default
    private List<Peluquero> peluqueros = new ArrayList<>();

    // Variables estadísticas
    private double acumuladorCostos;
    private double acumuladorGanancias;
    private double promedioRecaudacionDiaria;
    private int sillasNecesarias;

    // Lista con los clientes
    @Builder.Default
    private List<Cliente> clientes = new ArrayList<>();

    // ------------------------------------------------------------------------------------
    // ---- Funciones encargadas de calcular los valores de las variables aleatorias ------
    // ------------------------------------------------------------------------------------

    public float calcularProximaLlegada(float a, float b) {
        float random = (float) Math.random();

        float tiempoEntreLlegadas = DistribucionUniforme.generar(random, a, b);

        this.random1 = random;
        this.tiempoEntreLlegadas = tiempoEntreLlegadas;
        this.proximaLlegada = relojActual + tiempoEntreLlegadas;

        return proximaLlegada;
    }

    public Peluquero determinarQuienLoAtiende(float[] probabilidades) {
        float random = (float) Math.random();
        Peluquero peluquero = null;

        for (int i = 0; i < probabilidades.length; i++) {
            if (random < probabilidades[i]) {
                peluquero = peluqueros.get(i);
                break;
            }
        }

        this.random2 = random;
        this.nombreQuienAtiende = peluquero.getNombre();

        return peluquero;
    }

    public float determinarFinAtencionOtrosPeluqueros(Peluquero peluquero) {
        float random = (float) Math.random();
        float a = peluquero.getMin();
        float b = peluquero.getMax();

        float tiempoAtencion = DistribucionUniforme.generar(random, a, b);
        float finAtencion = relojActual + tiempoAtencion;

        this.random4 = random;
        this.tiempoAtencion = tiempoAtencion;

        return finAtencion;
    }

    public float determinarFinAtencionVeteranoA(Peluquero peluquero, RungeKutta rk) {
        // Generamos valor aleatorio para la complejidad
        float random = (float) Math.random();
        float complejidad = DistribucionUniforme.generar(random, peluquero.getMin(), peluquero.getMax());

        // Buscamos el X correspondiente en la tabla
        float rungekutta = rk.encontrarX(complejidad);

        // Calculamos el fin de atención
        float tiempoAtencion = rungekutta * 100;
        float finAtencion = relojActual + tiempoAtencion;

        this.random3 = random;
        this.complejidad = complejidad;
        this.rungeKutta = rungekutta;
        this.tiempoAtencion = tiempoAtencion;

        return finAtencion;
    }

    // ------------------------------------------------------------------------------------
    // ------------------- Funciones con la lógica de la simulación -----------------------
    // ------------------------------------------------------------------------------------

    /**
     * Calcula y setea los atributos:
     * - relojTotal
     * - horaTotal
     * - relojActual
     * - horaActual
     * - dia
     *
     * @param vectorAnterior: vector estado de la fila anterior
     * @return EVENTO de la fila actual
     */
    public Evento determinarEvento(VectorEstado vectorAnterior) {
        float proximoReloj;
        Evento proximoEvento;

        this.dia = vectorAnterior.getDia();
        this.iteracion = vectorAnterior.getIteracion();

        Float proximaLlegada = vectorAnterior.getProximaLlegada();

        // For para buscar el fin de atención más chico
        float finAtencionMin = Float.MAX_VALUE;
        for (Peluquero p : vectorAnterior.getPeluqueros()) {
            Float finAtencion = p.getFinAtencion();
            if (finAtencion != null && finAtencion < finAtencionMin) {
                finAtencionMin = finAtencion;
            }
        }

        // Si proximaLlegada no es null, es porque ya pasaron 8 horas y ya no recibimos más clientes
        if (proximaLlegada != null) {

            // Recibir cliente
            if (proximaLlegada < finAtencionMin && proximaLlegada < 480) {
                proximoReloj = proximaLlegada;
                proximoEvento = Evento.LLEGADA_CLIENTE;

            // Finalizar atención
            } else {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;
            }

        // Si proximaLlegada es null, vamos a finalizar atenciones hasta que la lista de clientes esté vacía
        } else {

            // Finalizar atención
            if (!vectorAnterior.getClientes().isEmpty()) {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;

            // Iniciar un nuevo día
            } else {
                proximoReloj = 0;
                proximoEvento = Evento.INICIALIZACION;
            }
        }

        // Setear relojes
        this.iteracion++;
        this.relojActual = proximoReloj;
        this.relojTotal = Math.max(relojActual - vectorAnterior.getRelojActual(), 0) + vectorAnterior.getRelojTotal();
        this.horaActual = (int) Math.ceil(relojActual / 60);
        this.horaTotal = (int) Math.ceil(relojTotal / 60);
        if (proximoReloj == 0) {
            this.dia = vectorAnterior.getDia() + 1;
        }

        return proximoEvento;
    }


    /**
     * Este método duplica en el nuevo vector estado todos aquellos atributos que necesitamos.
     * En caso de que sea evento INICIALIZACIÓN, setea los atributos relacionados a la próxima llegada.
     * Atributos que duplica:
     * - peluqueros
     * - clientes
     * - acumuladorGanancias
     * - acumuladorCostos
     * - proximaLlegada (en caso de que sea evento FIN_ATENCION)
     *
     * @param vectorAnterior:   vector estado de la fila anterior
     * @param evento:           evento de la fila actual
     * @param tiempoLlegadaMin: tiempo mínimo de llegada de los clientes
     * @param tiempoLlegadaMax: tiempo máximo de llegada de los clientes
     */
    public void duplicarVector(VectorEstado vectorAnterior, Evento evento, float tiempoLlegadaMin, float tiempoLlegadaMax) {
        this.acumuladorGanancias = vectorAnterior.getAcumuladorGanancias();
        this.acumuladorCostos = vectorAnterior.getAcumuladorCostos();
        this.sillasNecesarias = vectorAnterior.getSillasNecesarias();

        this.peluqueros = new ArrayList<>();
        for (Peluquero peluquero : vectorAnterior.getPeluqueros()) {
            this.peluqueros.add(peluquero.clone());
        }
        this.clientes = new ArrayList<>();
        for (Cliente cliente : vectorAnterior.getClientes()) {
            this.clientes.add(cliente.clone());
        }

        Float proximaLlegada = vectorAnterior.getProximaLlegada();
        if (evento == Evento.FIN_ATENCION && proximaLlegada != null && proximaLlegada < 480) {
            this.proximaLlegada = vectorAnterior.getProximaLlegada();
        }

        if (evento == Evento.INICIALIZACION) {
            float random = (float) Math.random();
            float tiempoEntreLlegadas = DistribucionUniforme.generar(random, tiempoLlegadaMin, tiempoLlegadaMax);
            this.random1 = random;
            this.tiempoEntreLlegadas = tiempoEntreLlegadas;
            this.proximaLlegada = tiempoEntreLlegadas;
        }
    }

    /**
     * Este método se llama al final de simular toda la fila, y actualiza todas las variables estadísticas.
     * Actualiza:
     * - Tiempos de espera de los clientes que estaban esperando
     * - sillasNecesarias
     * - promedioRecaudacionDiaria
     */
    public void actualizarVariablesEstadisticas() {
        for (Cliente c : clientes) {
            if (c.getEstado() == EstadoCliente.ESPERANDO_ATENCION) {
                actualizarAcumulador(c);
                actualizarCliente(c);

                if (c.getAcumuladorTiempoEspera() >= 30) {
                    this.acumuladorCostos += 1500;
                }
            }
        }

        // (Máximo valor entre: valor anterior y la suma de clientes actualmente en cola)
        this.sillasNecesarias = Math.max(sillasNecesarias, peluqueros.stream().mapToInt(Peluquero::getCola).sum());

        this.promedioRecaudacionDiaria = (acumuladorGanancias - acumuladorCostos) / dia;
    }

    // ------------------------------------------------------------------------------------
    // ---------------------------- Funciones auxiliares ----------------------------------
    // ------------------------------------------------------------------------------------

    public Peluquero determinarQuePeluqueroFinalizoAtencion() {
        Peluquero peluquero = null;

        for (Peluquero value : peluqueros) {
            peluquero = value;
            Float finAtencion = peluquero.getFinAtencion();
            if (finAtencion != null && finAtencion == relojActual) {
                break;
            }
        }

        this.peluqueros.get(peluquero.getId()).setFinAtencion(null);

        return peluquero;
    }

    public Cliente buscarCliente(Peluquero peluquero, EstadoCliente estado) {
        for (Cliente cliente : clientes) {
            if (cliente.getPeluquero().getId() == peluquero.getId() && cliente.getEstado() == estado) {
                return cliente;
            }
        }
        return null;
    }

    public void actualizarStringEvento(String stringEvento) {
        this.evento = stringEvento;
    }

    public void agregarCliente(Cliente cliente) {
        this.clientes.add(cliente.clone());
    }

    public void quitarCliente(Cliente cliente) {
        this.clientes.remove(cliente);
    }

    public void actualizarPeluquero(Peluquero peluquero, Float finAtencion) {
        peluquero.setFinAtencion(finAtencion);
        Peluquero peluqueroActualizado = peluquero.clone();
        this.peluqueros.set(peluquero.getId(), peluqueroActualizado);
    }

    public Float obtenerFinAtencionfromPeluquero(Peluquero peluquero) {
        return peluqueros.get(peluquero.getId()).getFinAtencion();
    }

    public void actualizarCliente(Cliente cliente) {
        Cliente clienteActualizado = cliente.clone();
        List<Cliente> listaActualizada = new ArrayList<>();

        for (Cliente c : clientes) {
            if (c.getId() == cliente.getId()) {
                listaActualizada.add(clienteActualizado);
            } else {
                listaActualizada.add(c);
            }
        }

        this.clientes = listaActualizada;
    }

    public void actualizarAcumulador(Cliente cliente) {
        cliente.actualizarAcumulador(relojActual);

        if (cliente.getAcumuladorTiempoEspera() >= 30) {
            this.acumuladorCostos += 1500;
        }
    }

    public void cobrarAtencion(Peluquero peluquero) {
        this.acumuladorGanancias += peluquero.getTarifa();
    }

    // Cortamos la simulación cuando terminó el día n o cuando llegamos a 100 mil iteraciones
    public boolean esLaUltimaFila(int n, int contador) {
        return proximaLlegada == null && clientes.isEmpty() && dia == n || contador == 100000;
    }

}