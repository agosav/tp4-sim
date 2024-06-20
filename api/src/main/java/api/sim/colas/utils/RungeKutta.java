package api.sim.colas.utils;

import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class RungeKutta {

    private float h;
    private float x0;
    private float C0;
    private float primerNum;
    private float segundoNum;
    private float tercerNum;
    private float complejidad;
    private List<float[]> tabla;

    public void realizarTabla() {
        tabla = new ArrayList<>();

        // Agregar la condición inicial a la tabla
        tabla.add(new float[]{x0, C0, 0, 0, 0, 0, x0, C0});

        while (true) {
            int i = tabla.size() - 1;
            float currentX = tabla.get(i)[6];
            float C = tabla.get(i)[7];

            // Calculamos k1, k2, k3 y k4
            float k1 = h * (primerNum * (C + segundoNum) * (C + segundoNum) + tercerNum);
            float k2 = h * (primerNum * (C + 0.5f * k1 + segundoNum) * (C + 0.5f * k1 + segundoNum) + tercerNum);
            float k3 = h * (primerNum * (C + 0.5f * k2 + segundoNum) * (C + 0.5f * k2 + segundoNum) + tercerNum);
            float k4 = h * (primerNum * (C + k3 + segundoNum) * (C + k3 + segundoNum) + tercerNum);

            // Actualizamos el siguiente valor de C
            float Cnext = C + (k1 + 2 * k2 + 2 * k3 + k4) / 6;

            // Actualizamos el siguiente valor de currentX
            float xnext = currentX + h;

            // Agregamos los nuevos valores a la tabla
            tabla.add(new float[]{currentX, C, k1, k2, k3, k4, xnext, Cnext});

            // Si C supera el valor de complejidad, cortar el cálculo
            if (C > complejidad) {
                break;
            }
        }
    }

    public float encontrarX(float complejidad) {
        for (float[] row : tabla) {
            if (row[1] > complejidad) {
                return row[0];
            }
        }
        return -1;
    }

}
