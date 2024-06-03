package api.sim.colas.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParametrosDto {

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo_llegada_min")
    private float tiempoLlegadaMin;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo_llegada_max")
    private float tiempoLlegadaMax;

    @PositiveOrZero
    @NotNull
    @JsonProperty("porcentaje_aprendiz")
    private float porcentajeAprendiz;

    @PositiveOrZero
    @NotNull
    @JsonProperty("porcentaje_veterano_a")
    private float porcentajeVeteranoA;

    @PositiveOrZero
    @NotNull
    @JsonProperty("porcentaje_veterano_b")
    private float porcentajeVeteranoB;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo_atencion_min_aprendiz")
    private float tiempoAtencionMinAprendiz;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo_atencion_min_veterano_a")
    private float tiempoAtencionMinVeteranoA;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo_atencion_min_veterano_b")
    private float tiempoAtencionMinVeteranoB;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo-atencion_max_aprendiz")
    private float tiempoAtencionMaxAprendiz;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo-atencion_max_veterano_a")
    private float tiempoAtencionMaxVeteranoA;

    @PositiveOrZero
    @NotNull
    @JsonProperty("tiempo-atencion_max_veterano_b")
    private float tiempoAtencionMaxVeteranoB;

    @NotNull
    @Positive
    private int n;

    @Max(100000)
    @NotNull
    @PositiveOrZero
    private int i;

    @Positive
    @NotNull
    private int j;
}
