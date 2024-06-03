package api.sim.colas.dtos;


import api.sim.colas.enums.EstadoCliente;
import api.sim.colas.enums.Evento;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;
import api.sim.colas.utils.Auxiliar;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VectorEstado {

    @Builder.Default
    private String evento = "Inicialización";

    private float relojTotal;

    private float relojDia;

    private int hora;

    @Builder.Default
    private int dia = 1;

    private Float random1;

    private Float tiempoEntreLlegadas;

    private Float proximaLlegada;

    private Float random2;

    private String nombreQuienAtiende;

    private Float random3;

    private Float tiempoAtencion;

    @Builder.Default
    private List<Peluquero> peluqueros = new ArrayList<>();

    @Builder.Default
    private List<Cliente> listaClientes = new ArrayList<>();

    // ------------------------------------------------------------------------------------
    //  Funciones encargadas de determinar calcular los valores de las variables aleatorias
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
    // ------------------------------- Otras funciones ------------------------------------
    // ------------------------------------------------------------------------------------

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
            if (proximaLlegada < finAtencionMin && hora <= 8) {
                proximoReloj = proximaLlegada;
                proximoEvento = Evento.LLEGADA_CLIENTE;
            } else {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;
            }
        } else {
            if (!vectorAnterior.getListaClientes().isEmpty()) {
                proximoReloj = finAtencionMin;
                proximoEvento = Evento.FIN_ATENCION;
            } else {
                proximoReloj = 0;
                proximoEvento = Evento.INICIALIZACION;
            }
        }

        this.relojDia = proximoReloj;
        this.relojTotal = Math.max(relojDia - vectorAnterior.getRelojDia(), 0) + vectorAnterior.getRelojTotal();

        if (proximoReloj == 0) {
            this.dia = vectorAnterior.getDia() + 1;
        }

        this.hora = (int) Math.ceil(relojDia / 60);

        return proximoEvento;
    }

    public void duplicarVector(VectorEstado vectorAnterior, Evento evento, float tiempoLlegadaMin, float tiempoLlegadaMax) {
        this.peluqueros = new ArrayList<>();
        for (Peluquero peluquero : vectorAnterior.getPeluqueros()) {
            this.peluqueros.add(Auxiliar.construirPeluquero(peluquero, peluquero.getFinAtencion()));
        }
        this.listaClientes = new ArrayList<>();
        for (Cliente cliente : vectorAnterior.getListaClientes()) {
            this.listaClientes.add(Auxiliar.construirCliente(cliente));
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

    public void actualizarStringEvento(String stringEvento) {
        this.evento = stringEvento;
    }

    public void agregarCliente(Cliente cliente) {
        this.listaClientes.add(Auxiliar.construirCliente(cliente));
    }

    public void quitarCliente(Cliente cliente) {
        this.listaClientes.remove(cliente);
    }

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
        return listaClientes.stream()
                .filter(c -> c.getPeluquero().getId() == peluquero.getId() && c.getEstado() == EstadoCliente.SIENDO_ATENDIDO)
                .findFirst()
                .orElse(null);
    }

    public Cliente determinarProximoClienteEnCola(Peluquero peluquero) {
        return listaClientes.stream()
                .filter(c -> c.getPeluquero().getId() == peluquero.getId() && c.getEstado() == EstadoCliente.ESPERANDO_ATENCION)
                .findFirst()
                .orElse(null);
    }

    public void actualizarPeluquero(Peluquero peluquero, Float finAtencion) {
        Peluquero peluqueroActualizado = Auxiliar.construirPeluquero(peluquero, finAtencion);
        this.peluqueros.set(peluquero.getId(), peluqueroActualizado);
    }

    public Float obtenerFinAtencionfromPeluquero(Peluquero peluquero) {
        return peluqueros.get(peluquero.getId()).getFinAtencion();
    }

    public void actualizarCliente(Cliente cliente) {
        Cliente clienteActualizado = Auxiliar.construirCliente(cliente);
        List<Cliente> listaActualizada = new ArrayList<>();

        for (Cliente c : listaClientes) {
            if (c.getId() == cliente.getId()) {
                listaActualizada.add(clienteActualizado);
            } else {
                listaActualizada.add(c);
            }
        }

        this.listaClientes = listaActualizada;
    }

}