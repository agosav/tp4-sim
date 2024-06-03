package api.sim.colas.utils;

import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.objetos.Cliente;
import api.sim.colas.objetos.Peluquero;

import java.util.List;

public class Auxiliar {

    public static List<Peluquero> inicializarPeluqueros(ParametrosDto dto) {
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

    // Este método es para acumular las probabilidades en una lista para usar después.
    // [15, 45, 40] -> [0.15, 0.6, 1]
    public static float[] calcularProbabilidadesAcumuladas(ParametrosDto dto) {
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

    public static Peluquero construirPeluquero(Peluquero original, Float finAtencion) {
        return Peluquero.builder()
                .id(original.getId())
                .nombre(original.getNombre())
                .estado(original.getEstado())
                .tiempoAtencionMax(original.getTiempoAtencionMax())
                .tiempoAtencionMin(original.getTiempoAtencionMin())
                .cola(original.getCola())
                .tarifa(original.getTarifa())
                .finAtencion(finAtencion)
                .build();
    }

    public static Cliente construirCliente(Cliente original) {
        return Cliente.builder()
                .id(original.getId())
                .estado(original.getEstado())
                .acumuladorTiempoEspera(original.getAcumuladorTiempoEspera())
                .peluquero(original.getPeluquero())
                .build();
    }
}
