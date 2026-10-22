package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.List;

/** Takes the first free window seats (A or F) in row order. */
public class WindowSeatStrategy implements SeatAssignmentStrategy {

    @Override
    public List<Seat> assign(Flight flight, SeatRequest request) {
        List<Seat> windows = flight.freeSeats().stream()
                .filter(Seat::isWindow)
                .limit(request.groupSize())
                .toList();
        return windows.size() == request.groupSize() ? windows : List.of();
    }
}
