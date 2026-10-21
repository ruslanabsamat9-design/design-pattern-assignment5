package flight.data;

/**
 * Our own interface for currency rates (Clean Code, Ch. 10: "isolate from change").
 * The fare code depends on this interface, never on a concrete rate source.
 */
public interface ExchangeRateProvider {

    /** How many units of {@code to} one unit of {@code from} costs. */
    double getRate(String from, String to);
}
