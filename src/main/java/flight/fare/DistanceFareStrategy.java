package flight.fare;

import flight.model.FlightRequest;

/** Price = distance * base rate + fixed tax. */
public class DistanceFareStrategy implements FareStrategy {

    @Override
    public double calculate(FlightRequest request) {
        return Prices.round2(request.distanceKm() * Prices.BASE_RATE_PER_KM + Prices.TAX);
    }
}
