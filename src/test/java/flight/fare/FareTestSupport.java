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
        return new FareCalculator(type, rates);
    }
}
