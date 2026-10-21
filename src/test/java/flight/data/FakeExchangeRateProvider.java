package flight.data;

/** Test double: returns one fixed rate for every currency pair. */
public class FakeExchangeRateProvider implements ExchangeRateProvider {

    private final double fixedRate;

    public FakeExchangeRateProvider(double fixedRate) {
        this.fixedRate = fixedRate;
    }

    @Override
    public double getRate(String from, String to) {
        return fixedRate;
    }
}
