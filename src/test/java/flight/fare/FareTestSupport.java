package flight.fare;

import flight.data.ExchangeRateProvider;

/**
 * Small adapter so that FareBehaviourTest stays byte-for-byte identical
 * on the "before" and the "after" commit. Only this adapter changes.
 */
final class FareTestSupport {

    private FareTestSupport() {
    }

    static FareCalculator calculatorFor(FareType type, ExchangeRateProvider rates) {
        FareStrategy strategy = switch (type) {
            case DISTANCE -> new DistanceFareStrategy();
            case DEMAND -> new DemandFareStrategy();
            case LAST_MINUTE -> new LastMinuteFareStrategy();
            case INTERNATIONAL -> new InternationalFareStrategy(rates);
        };
        return new FareCalculator(strategy);
    }
}
