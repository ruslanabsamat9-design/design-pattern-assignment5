package flight.fare;

import flight.model.FlightRequest;

/** Price grows in steps as the plane fills up. */
public class DemandFareStrategy implements FareStrategy {

    @Override
    public double calculate(FlightRequest request) {
        double base = request.distanceKm() * Prices.BASE_RATE_PER_KM;
        return Prices.round2(base * factorFor(request.occupancyPercent()));
    }

    private double factorFor(int occupancyPercent) {
        if (occupancyPercent >= 80) {
            return 1.8;
        }
        if (occupancyPercent >= 50) {
            return 1.3;
        }
        return 1.0;
    }
}
