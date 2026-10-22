package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.List;
import java.util.Objects;

/**
 * Context of the seat family. It only holds a strategy and delegates to it.
 * It has no if / switch / instanceof on the strategy type.
 */
public class SeatAssigner {

    private SeatAssignmentStrategy strategy;

    public SeatAssigner(SeatAssignmentStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(SeatAssignmentStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public SeatAssignmentStrategy getStrategy() {
        return strategy;
    }

    public List<Seat> assign(Flight flight, SeatRequest request) {
        return strategy.assign(flight, request);
    }
}
