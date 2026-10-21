package flight.data;

import java.util.Map;

/**
 * Simulated rate source: a fixed built-in table. No real API is called.
 */
public class SimulatedExchangeRateProvider implements ExchangeRateProvider {

    private static final Map<String, Double> TO_KZT = Map.of(
            "USD", 505.0,
            "EUR", 590.0,
            "KZT", 1.0);

    @Override
    public double getRate(String from, String to) {
        if (!"KZT".equals(to) || !TO_KZT.containsKey(from)) {
            throw new IllegalArgumentException("Unsupported pair: " + from + "->" + to);
        }
        return TO_KZT.get(from);
    }
}
