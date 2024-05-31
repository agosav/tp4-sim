package api.sim.colas.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParametrosDto {

    @JsonProperty("tiempo_llegada_min")
    private float tiempoLlegadaMin;

    @JsonProperty("tiempo_llegada_max")
    private float tiempoLlegadaMax;

    @JsonProperty("porcentaje_aprendiz")
    private float porcentajeAprendiz;

    @JsonProperty("porcentaje_veterano_a")
    private float porcentajeVeteranoA;

    @JsonProperty("porcentaje_veterano_b")
    private float porcentajeVeteranoB;

    @JsonProperty("tiempo_atencion_min_aprendiz")
    private float tiempoAtencionMinAprendiz;

    @JsonProperty("tiempo_atencion_min_veterano_a")
    private float tiempoAtencionMinVeteranoA;

    @JsonProperty("tiempo_atencion_min_veterano_b")
    private float tiempoAtencionMinVeteranoB;

    @JsonProperty("tiempo-atencion_max_aprendiz")
    private float tiempoAtencionMaxAprendiz;

    @JsonProperty("tiempo-atencion_max_veterano_a")
    private float tiempoAtencionMaxVeteranoA;

    @JsonProperty("tiempo-atencion_max_veterano_b")
    private float tiempoAtencionMaxVeteranoB;

    private int n;

    private int i;

    private int j;
}
