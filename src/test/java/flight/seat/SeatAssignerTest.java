package flight.seat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatPreference;
import flight.model.SeatRequest;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the context only: hold a strategy, delegate, allow replacing it. */
class SeatAssignerTest {

    private final Flight flight = new Flight("T", 3);
    private final SeatRequest request = new SeatRequest(1, SeatPreference.NONE);

    @Test
    void delegatesToTheStrategy() {
        SeatAssigner assigner = new SeatAssigner((f, r) -> List.of(new Seat(3, 'F')));
        assertEquals(List.of(new Seat(3, 'F')), assigner.assign(flight, request));
    }

    @Test
    void setStrategy_changesBehaviourAtRuntime() {
        SeatAssigner assigner = new SeatAssigner(new FrontRowsFirstStrategy());
        assertEquals(List.of(new Seat(1, 'A')), assigner.assign(flight, request));
        assigner.setStrategy(new WindowSeatStrategy());
        assertSame(WindowSeatStrategy.class, assigner.getStrategy().getClass());
        assigner.setStrategy((f, r) -> List.of(new Seat(2, 'B')));
        assertEquals(List.of(new Seat(2, 'B')), assigner.assign(flight, request));
    }

    @Test
    void nullStrategy_isRejected() {
        assertThrows(NullPointerException.class, () -> new SeatAssigner(null));
    }
}
