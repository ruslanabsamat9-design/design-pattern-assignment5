package flight.fare;

import flight.model.FlightRequest;
import java.util.Objects;

/**
 * Context of the fare family. It only holds a strategy and delegates to it.
 * It has no if / switch / instanceof on the strategy type.
 */
public class FareCalculator {

    private FareStrategy strategy;

    public FareCalculator(FareStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(FareStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public FareStrategy getStrategy() {
        return strategy;
    }

    public double calculate(FlightRequest request) {
        return strategy.calculate(request);
    }
}
