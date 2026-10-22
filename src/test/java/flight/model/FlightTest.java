package flight.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class FlightTest {

    @Test
    void newFlight_isEmpty() {
        Flight flight = new Flight("T1", 10);
        assertEquals(60, flight.totalSeats());
        assertEquals(60, flight.freeSeats().size());
        assertEquals(0, flight.occupancyPercent());
    }

    @Test
    void book_marksSeatsAsTaken() {
        Flight flight = new Flight("T1", 10);
        flight.book(List.of(new Seat(1, 'A'), new Seat(1, 'B')));
        assertFalse(flight.isFree(new Seat(1, 'A')));
        assertTrue(flight.isFree(new Seat(1, 'C')));
        assertEquals(58, flight.freeSeats().size());
    }

    @Test
    void book_takenSeat_failsAndChangesNothing() {
        Flight flight = new Flight("T1", 2);
        flight.book(List.of(new Seat(1, 'A')));
        assertThrows(IllegalStateException.class,
                () -> flight.book(List.of(new Seat(1, 'B'), new Seat(1, 'A'))));
        assertTrue(flight.isFree(new Seat(1, 'B')));
    }

    @Test
    void occupancyPercent_followsBookings() {
        Flight flight = new Flight("T1", 1);
        flight.book(List.of(new Seat(1, 'A'), new Seat(1, 'B'), new Seat(1, 'C')));
        assertEquals(50, flight.occupancyPercent());
    }

    @Test
    void seat_withBadLetter_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Seat(1, 'Z'));
    }
}
