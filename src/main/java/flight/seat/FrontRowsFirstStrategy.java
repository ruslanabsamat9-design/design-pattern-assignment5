package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.List;

/** Takes the first free seats starting from the front of the plane. */
public class FrontRowsFirstStrategy implements SeatAssignmentStrategy {

    @Override
    public List<Seat> assign(Flight flight, SeatRequest request) {
        List<Seat> first = flight.freeSeats().stream()
                .limit(request.groupSize())
                .toList();
        return first.size() == request.groupSize() ? first : List.of();
    }
}
