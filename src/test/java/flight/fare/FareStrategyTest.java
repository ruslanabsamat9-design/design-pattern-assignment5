package flight.fare;

import static org.junit.jupiter.api.Assertions.assertEquals;

import flight.data.FakeExchangeRateProvider;
import flight.model.FlightRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** One nested group of tests per strategy, calling the strategy directly. */
class FareStrategyTest {

    private static FlightRequest request(double km, int days, int occupancy, boolean international) {
        return new FlightRequest("NQZ", "ALA", km, days, occupancy, international);
    }

    @Nested
    class DistanceFare {
        private final FareStrategy strategy = new DistanceFareStrategy();

        @Test
        void zeroDistance_costsOnlyTax() {
            assertEquals(3000.0, strategy.calculate(request(0, 30, 0, false)), 0.01);
        }

        @Test
        void ignoresOccupancyAndDate() {
            assertEquals(strategy.calculate(request(500, 30, 0, false)),
                    strategy.calculate(request(500, 1, 100, false)), 0.01);
        }
    }

    @Nested
    class DemandFare {
        private final FareStrategy strategy = new DemandFareStrategy();

        @Test
        void boundaries() {
            assertEquals(25000.0, strategy.calculate(request(1000, 30, 49, false)), 0.01);
            assertEquals(32500.0, strategy.calculate(request(1000, 30, 50, false)), 0.01);
            assertEquals(32500.0, strategy.calculate(request(1000, 30, 79, false)), 0.01);
            assertEquals(45000.0, strategy.calculate(request(1000, 30, 80, false)), 0.01);
        }
    }

    @Nested
    class LastMinuteFare {
        private final FareStrategy strategy = new LastMinuteFareStrategy();

        @Test
        void boundaries() {
            assertEquals(47500.0, strategy.calculate(request(1000, 2, 0, false)), 0.01);
            assertEquals(35000.0, strategy.calculate(request(1000, 3, 0, false)), 0.01);
            assertEquals(35000.0, strategy.calculate(request(1000, 6, 0, false)), 0.01);
            assertEquals(28750.0, strategy.calculate(request(1000, 7, 0, false)), 0.01);
            assertEquals(28750.0, strategy.calculate(request(1000, 14, 0, false)), 0.01);
            assertEquals(25000.0, strategy.calculate(request(1000, 15, 0, false)), 0.01);
        }
    }

    @Nested
    class InternationalFare {
        @Test
        void usesTheInjectedRate() {
            FareStrategy at500 = new InternationalFareStrategy(new FakeExchangeRateProvider(500.0));
            FareStrategy at400 = new InternationalFareStrategy(new FakeExchangeRateProvider(400.0));
            assertEquals(42500.0, at500.calculate(request(1000, 30, 0, true)), 0.01);
            assertEquals(34000.0, at400.calculate(request(1000, 30, 0, true)), 0.01);
        }
    }
}
