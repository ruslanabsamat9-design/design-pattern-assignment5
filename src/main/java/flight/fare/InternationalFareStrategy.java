package flight.fare;

import flight.data.ExchangeRateProvider;
import flight.model.FlightRequest;

/**
 * International tickets are priced in USD and converted to KZT.
 * The rate comes from our own {@link ExchangeRateProvider} interface,
 * so this class never knows which real source stands behind it.
 */
public class InternationalFareStrategy implements FareStrategy {

    private static final double USD_PER_KM = 0.06;
    private static final double USD_AIRPORT_FEE = 25.0;

    private final ExchangeRateProvider rates;

    public InternationalFareStrategy(ExchangeRateProvider rates) {
        this.rates = rates;
    }

    @Override
    public double calculate(FlightRequest request) {
        double usd = request.distanceKm() * USD_PER_KM + USD_AIRPORT_FEE;
        return Prices.round2(usd * rates.getRate("USD", "KZT"));
    }
}
