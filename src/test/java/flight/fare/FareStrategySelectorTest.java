package flight.fare;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import flight.data.FakeExchangeRateProvider;
import flight.model.FlightRequest;
import org.junit.jupiter.api.Test;

/** One test per Selector rule, plus rule order and boundaries. */
class FareStrategySelectorTest {

    private final FareStrategySelector selector =
            new FareStrategySelector(new FakeExchangeRateProvider(500.0));

    private static FlightRequest request(int days, int occupancy, boolean international) {
        return new FlightRequest("NQZ", "ALA", 1000, days, occupancy, international);
    }

    @Test
    void rule1_departureSoon_picksLastMinute() {
        assertInstanceOf(LastMinuteFareStrategy.class, selector.select(request(2, 10, false)));
    }

    @Test
    void rule2_fullFlight_picksDemand() {
        assertInstanceOf(DemandFareStrategy.class, selector.select(request(10, 85, false)));
    }

    @Test
    void rule3_internationalFlight_picksInternational() {
        assertInstanceOf(InternationalFareStrategy.class, selector.select(request(10, 40, true)));
    }

    @Test
    void rule4_otherwise_picksDistance() {
        assertInstanceOf(DistanceFareStrategy.class, selector.select(request(10, 40, false)));
    }

    @Test
    void lastMinuteBeatsEverythingElse() {
        assertInstanceOf(LastMinuteFareStrategy.class, selector.select(request(1, 95, true)));
    }

    @Test
    void demandBeatsInternational() {
        assertInstanceOf(DemandFareStrategy.class, selector.select(request(10, 95, true)));
    }

    @Test
    void boundaries() {
        assertInstanceOf(DistanceFareStrategy.class, selector.select(request(3, 79, false)));
        assertInstanceOf(DemandFareStrategy.class, selector.select(request(3, 80, false)));
        assertInstanceOf(LastMinuteFareStrategy.class, selector.select(request(2, 0, false)));
    }
}
