package flight.fare;

import static org.junit.jupiter.api.Assertions.assertTrue;

import flight.data.FakeExchangeRateProvider;
import flight.model.FlightRequest;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** The same checks run on EVERY strategy of the fare family. */
class FareStrategyParameterizedTest {

    static Stream<FareStrategy> allStrategies() {
        return Stream.of(
                new DistanceFareStrategy(),
                new DemandFareStrategy(),
                new LastMinuteFareStrategy(),
                new InternationalFareStrategy(new FakeExchangeRateProvider(500.0)));
    }

    @ParameterizedTest(name = "{0}: price is never negative")
    @MethodSource("allStrategies")
    void priceIsNeverNegative(FareStrategy strategy) {
        for (int km : new int[] {0, 1, 500, 12000}) {
            for (int days : new int[] {0, 5, 40}) {
                for (int occupancy : new int[] {0, 60, 100}) {
                    FlightRequest r = new FlightRequest("A", "B", km, days, occupancy, true);
                    assertTrue(strategy.calculate(r) >= 0, strategy.name() + " gave a negative price");
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}: longer flight is never cheaper")
    @MethodSource("allStrategies")
    void longerFlightIsNeverCheaper(FareStrategy strategy) {
        FlightRequest shorter = new FlightRequest("A", "B", 500, 10, 60, true);
        FlightRequest longer = new FlightRequest("A", "B", 5000, 10, 60, true);
        assertTrue(strategy.calculate(longer) >= strategy.calculate(shorter));
    }
}
