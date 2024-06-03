package api.sim.colas.utils;

public class Distribucion {
    public static float uniforme(float random, float a, float b) {
        return a + (b - a) * random;
    }
}
