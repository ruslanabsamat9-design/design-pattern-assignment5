package flight.seat;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatRequest;
import java.util.ArrayList;
import java.util.List;

/** Looks for a run of neighbouring free seats (consecutive letters) inside one row. */
public class GroupTogetherStrategy implements SeatAssignmentStrategy {

    @Override
    public List<Seat> assign(Flight flight, SeatRequest request) {
        for (int row = 1; row <= flight.getRows(); row++) {
            List<Seat> run = new ArrayList<>();
            for (char letter : Seat.LETTERS.toCharArray()) {
                Seat seat = new Seat(row, letter);
                if (flight.isFree(seat)) {
                    run.add(seat);
                    if (run.size() == request.groupSize()) {
                        return run;
                    }
                } else {
                    run.clear();
                }
            }
        }
        return List.of();
    }
}
