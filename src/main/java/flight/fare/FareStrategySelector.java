package flight.fare;

import flight.data.ExchangeRateProvider;
import flight.model.FlightRequest;

/**
 * Chooses a fare strategy automatically from the current situation.
 * This is the ONLY place where if / switch on the situation is allowed.
 *
 * Rules (first match wins):
 *   1. departure in 2 days or less        -> LastMinuteFareStrategy
 *   2. flight at least 80% full            -> DemandFareStrategy
 *   3. international flight                -> InternationalFareStrategy
 *   4. everything else                     -> DistanceFareStrategy
 */
public class FareStrategySelector {

    static final int LAST_MINUTE_DAYS = 2;
    static final int HIGH_DEMAND_PERCENT = 80;

    private final FareStrategy lastMinute = new LastMinuteFareStrategy();
    private final FareStrategy demand = new DemandFareStrategy();
    private final FareStrategy international;
    private final FareStrategy distance = new DistanceFareStrategy();

    public FareStrategySelector(ExchangeRateProvider rates) {
        this.international = new InternationalFareStrategy(rates);
    }

    public FareStrategy select(FlightRequest request) {
        if (request.daysBeforeDeparture() <= LAST_MINUTE_DAYS) {
            return lastMinute;
        }
        if (request.occupancyPercent() >= HIGH_DEMAND_PERCENT) {
            return demand;
        }
        if (request.international()) {
            return international;
        }
        return distance;
    }
}
