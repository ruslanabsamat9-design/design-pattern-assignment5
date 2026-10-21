package flight.fare;

import flight.model.FlightRequest;

/** Price grows the closer the departure date is. */
public class LastMinuteFareStrategy implements FareStrategy {

    @Override
    public double calculate(FlightRequest request) {
        double base = request.distanceKm() * Prices.BASE_RATE_PER_KM;
        return Prices.round2(base * factorFor(request.daysBeforeDeparture()));
    }

    private double factorFor(int daysBeforeDeparture) {
        if (daysBeforeDeparture <= 2) {
            return 1.9;
        }
        if (daysBeforeDeparture <= 6) {
            return 1.4;
        }
        if (daysBeforeDeparture <= 14) {
            return 1.15;
        }
        return 1.0;
    }
}
