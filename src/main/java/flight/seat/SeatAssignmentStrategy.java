package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.List;

/**
 * Strategy interface: one way of choosing seats for a request.
 * Implementations only READ the flight; they never book.
 */
public interface SeatAssignmentStrategy {

    /** Exactly {@code request.groupSize()} free seats, or an empty list if none can be found. */
    List<Seat> assign(Flight flight, SeatRequest request);

    default String name() {
        return getClass().getSimpleName();
    }
}
