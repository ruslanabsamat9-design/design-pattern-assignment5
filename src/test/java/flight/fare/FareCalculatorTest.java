package flight.fare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import flight.model.FlightRequest;
import org.junit.jupiter.api.Test;

/** Tests the context only: it must hold a strategy, delegate, and allow replacing it. */
class FareCalculatorTest {

    private final FlightRequest request = new FlightRequest("NQZ", "ALA", 1000, 30, 10, false);

    @Test
    void delegatesToTheStrategyFromTheConstructor() {
        FareCalculator calculator = new FareCalculator(r -> 111.0);
        assertEquals(111.0, calculator.calculate(request), 0.001);
    }

    @Test
    void setStrategy_changesBehaviourAtRuntime() {
        FareCalculator calculator = new FareCalculator(r -> 1.0);
        calculator.setStrategy(r -> 2.0);
        assertEquals(2.0, calculator.calculate(request), 0.001);
    }

    @Test
    void getStrategy_returnsTheCurrentOne() {
        FareStrategy first = new DistanceFareStrategy();
        FareStrategy second = new DemandFareStrategy();
        FareCalculator calculator = new FareCalculator(first);
        assertSame(first, calculator.getStrategy());
        calculator.setStrategy(second);
        assertSame(second, calculator.getStrategy());
    }

    @Test
    void nullStrategy_isRejected() {
        assertThrows(NullPointerException.class, () -> new FareCalculator(null));
        FareCalculator calculator = new FareCalculator(r -> 1.0);
        assertThrows(NullPointerException.class, () -> calculator.setStrategy(null));
    }
}
