package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.List;

/** Finds the row with the most free seats (lowest row wins a tie) and takes seats there. */
public class EmptyRowStrategy implements SeatAssignmentStrategy {

    @Override
    public List<Seat> assign(Flight flight, SeatRequest request) {
        List<Seat> bestRow = List.of();
        for (int row = 1; row <= flight.getRows(); row++) {
            List<Seat> free = flight.freeSeatsInRow(row);
            if (free.size() > bestRow.size()) {
                bestRow = free;
            }
        }
        if (bestRow.size() < request.groupSize()) {
            return List.of();
        }
        return bestRow.subList(0, request.groupSize());
    }
}
