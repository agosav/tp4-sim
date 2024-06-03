package api.sim.colas.dtos;

import api.sim.colas.enums.EstadoCliente;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VectorEstado {

    // String que especifica el evento simulado
    @Builder.Default
    private String evento = "Inicialización";

    // Acumulador de todos las minutos de la simulacion
    private float relojTotal;

    // Acumulador de todas las horas de la simulacion
    private int horaTotal;

    // Reloj en minutos del día actual
    private float relojDia;

    // Hora del día
    // Si el relojDia está entre 0 y 59, la hora es 1.
    // Si el relojDia está entre 60 y 119, la hora es 2. Etc
    private int horaDia;

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

    // Para determinar tiempo de atención
    private Float random3;
    private Float tiempoAtencion;

    // Lista con los peluqueros
    @Builder.Default
    private List<Peluquero> peluqueros = new ArrayList<>();

    // Variables estadísticas
    private float acumuladorCostos;
    private float acumuladorGanancias;
    private float promedioRecaudacionDiaria;
    private int sillasNecesarias;

    // Lista con los clientes
    @Builder.Default
    private List<Cliente> clientes = new ArrayList<>();

    // ------------------------------------------------------------------------------------
    // ---- Funciones encargadas de calcular los valores de las variables aleatorias ------
    // ------------------------------------------------------------------------------------

    public float calcularProximaLlegada(float a, float b) {
        float random = (float) Math.random();

        float tiempoEntreLlegadas = a + random * (b - a);

        this.random1 = random;
        this.tiempoEntreLlegadas = tiempoEntreLlegadas;
        this.proximaLlegada = relojDia + tiempoEntreLlegadas;

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

    public float determinarFinAtencion(Peluquero peluquero) {
        float random = (float) Math.random();
        float a = peluquero.getTiempoAtencionMin();
        float b = peluquero.getTiempoAtencionMax();

        float tiempoAtencion = a + random * (b - a);
        float finAtencion = relojDia + tiempoAtencion;

        this.random3 = random;
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
     * - relojDia
     * - horaDia
     * - dia
     *
     * @param vectorAnterior: vector estado de la fila anterior
     * @return EVENTO de la fila actual
     */
    public Evento determinarEvento(VectorEstado vectorAnterior) {
        this.dia = vectorAnterior.getDia();

        float proximoReloj;
        Evento proximoEvento;

        Float proximaLlegada = vectorAnterior.getProximaLlegada();
        float finAtencionMin = vectorAnterior.getPeluqueros().stream()
                .map(Peluquero::getFinAtencion)
                .filter(Objects::nonNull)
                .min(Float::compare)
                .orElse(Float.MAX_VALUE);

        if (proximaLlegada != null) {
            if (proximaLlegada < finAtencionMin && horaDia <= 8) {
                proximoReloj = proximaLlegada;
                proximoEvento = Evento.LLEGADA_CLIENTE;
            } else {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;
            }
        } else {
            if (!vectorAnterior.getClientes().isEmpty()) {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;
            } else {
                proximoReloj = 0;
                proximoEvento = Evento.INICIALIZACION;
            }
        }

        if (proximoReloj == 0) {
            this.dia = vectorAnterior.getDia() + 1;
        }

        this.relojDia = proximoReloj;
        this.relojTotal = Math.max(relojDia - vectorAnterior.getRelojDia(), 0) + vectorAnterior.getRelojTotal();

        this.horaDia = (int) Math.ceil(relojDia / 60);
        this.horaTotal = Math.max(horaDia - vectorAnterior.getHoraDia(), 0) + vectorAnterior.getHoraTotal();

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
            float tiempoEntreLlegadas = tiempoLlegadaMin + random * (tiempoLlegadaMax - tiempoLlegadaMin);
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
            }
        }

        // (Máximo valor entre: valor anterior y la suma de clientes actualmente en cola)
        this.sillasNecesarias = Math.max(sillasNecesarias, peluqueros.stream().mapToInt(Peluquero::getCola).sum());

        this.promedioRecaudacionDiaria = (acumuladorGanancias - acumuladorCostos) / dia;
    }

    /**
     * Este método actualiza el acumulador de tiempo de espera de UN cliente concreto que estaba esperando.
     *
     * @param cliente: cliente a actualizar
     */
    public void actualizarAcumulador(Cliente cliente) {
        float acumulador = relojDia - cliente.getLlegada();
        cliente.setAcumuladorTiempoEspera(acumulador);
        actualizarCliente(cliente);

        if (acumulador >= 30) {
            this.acumuladorCostos += 1500;
        }
    }

    // ------------------------------------------------------------------------------------
    // ---------------------------- Funciones auxiliares ----------------------------------
    // ------------------------------------------------------------------------------------

    public Peluquero determinarQuePeluqueroFinalizoAtencion() {
        Peluquero peluquero = null;

        for (Peluquero value : peluqueros) {
            peluquero = value;
            Float finAtencion = peluquero.getFinAtencion();
            if (finAtencion != null && finAtencion == relojDia) {
                break;
            }
        }

        this.peluqueros.get(peluquero.getId()).setFinAtencion(null);

        return peluquero;
    }

    public Cliente determinarClienteRecienAtendido(Peluquero peluquero) {
        return clientes.stream()
                .filter(c -> c.getPeluquero().getId() == peluquero.getId() && c.getEstado() == EstadoCliente.SIENDO_ATENDIDO)
                .findFirst()
                .orElse(null);
    }

    public Cliente determinarProximoClienteEnCola(Peluquero peluquero) {
        return clientes.stream()
                .filter(c -> c.getPeluquero().getId() == peluquero.getId() && c.getEstado() == EstadoCliente.ESPERANDO_ATENCION)
                .findFirst()
                .orElse(null);
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

    public void cobrarAtencion(Peluquero peluquero) {
        this.acumuladorGanancias += peluquero.getTarifa();
    }

}