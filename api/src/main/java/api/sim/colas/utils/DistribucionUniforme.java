package api.sim.colas.utils;

public class DistribucionUniforme {

    public static float generar(float random, float a, float b) {
        return a + (b - a) * random;
    }

    public static boolean esValido(float a, float b) {
        return a > b;
    }

}
