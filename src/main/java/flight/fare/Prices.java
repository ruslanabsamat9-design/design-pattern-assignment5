package flight.fare;

/** Shared money constants and rounding. */
public final class Prices {

    /** Base price per kilometre, in KZT. */
    public static final double BASE_RATE_PER_KM = 25.0;

    /** Fixed airport tax added by the distance fare, in KZT. */
    public static final double TAX = 3000.0;

    private Prices() {
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
