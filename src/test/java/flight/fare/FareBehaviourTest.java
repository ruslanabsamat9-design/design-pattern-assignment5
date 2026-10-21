package flight.fare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import flight.data.ExchangeRateProvider;
import flight.data.FakeExchangeRateProvider;
import flight.model.FlightRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Behaviour of every fare variant. Must pass on BOTH refactoring commits.
 * Fixed fake exchange rate: 1 USD = 500 KZT.
 */
class FareBehaviourTest {

    private final ExchangeRateProvider rates = new FakeExchangeRateProvider(500.0);

    private double price(FareType type, FlightRequest request) {
        return FareTestSupport.calculatorFor(type, rates).calculate(request);
    }

    private static FlightRequest request(double km, int days, int occupancy, boolean international) {
        return new FlightRequest("NQZ", "ALA", km, days, occupancy, international);
    }

    // ---- distance ----
    @Test
    void distanceFare_isKmTimesRatePlusTax() {
        assertEquals(28000.0, price(FareType.DISTANCE, request(1000, 30, 10, false)), 0.01);
    }

    // ---- demand ----
    @Test
    void demandFare_lowOccupancy_hasNoSurcharge() {
        assertEquals(25000.0, price(FareType.DEMAND, request(1000, 30, 30, false)), 0.01);
    }

    @Test
    void demandFare_mediumOccupancy_adds30Percent() {
        assertEquals(32500.0, price(FareType.DEMAND, request(1000, 30, 60, false)), 0.01);
    }

    @Test
    void demandFare_highOccupancy_adds80Percent() {
        assertEquals(45000.0, price(FareType.DEMAND, request(1000, 30, 90, false)), 0.01);
    }

    // ---- last minute ----
    @Test
    void lastMinuteFare_oneDayBefore_isMostExpensive() {
        assertEquals(47500.0, price(FareType.LAST_MINUTE, request(1000, 1, 10, false)), 0.01);
    }

    @Test
    void lastMinuteFare_tenDaysBefore_adds15Percent() {
        assertEquals(28750.0, price(FareType.LAST_MINUTE, request(1000, 10, 10, false)), 0.01);
    }

    @Test
    void lastMinuteFare_farInAdvance_hasNoSurcharge() {
        assertEquals(25000.0, price(FareType.LAST_MINUTE, request(1000, 30, 10, false)), 0.01);
    }

    // ---- international (uses the exchange rate source) ----
    @Test
    void internationalFare_convertsUsdWithExchangeRate() {
        // (1000 km * 0.06 + 25 USD) * 500 = 42500
        assertEquals(42500.0, price(FareType.INTERNATIONAL, request(1000, 30, 10, true)), 0.01);
    }

    // ---- same check for every variant ----
    @ParameterizedTest
    @EnumSource(FareType.class)
    void price_isNeverNegative(FareType type) {
        assertTrue(price(type, request(0, 0, 0, false)) >= 0);
        assertTrue(price(type, request(1000, 30, 50, true)) >= 0);
        assertTrue(price(type, request(12000, 1, 100, true)) >= 0);
    }

    @Test
    void request_withNegativeDistance_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> request(-1, 5, 10, false));
    }
}
