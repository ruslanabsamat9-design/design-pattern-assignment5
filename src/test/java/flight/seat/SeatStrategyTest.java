package flight.seat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatPreference;
import flight.model.SeatRequest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** One nested group of tests per seat strategy. */
class SeatStrategyTest {

    private static Seat s(int row, char letter) {
        return new Seat(row, letter);
    }

    private static SeatRequest one() {
        return new SeatRequest(1, SeatPreference.NONE);
    }

    private static SeatRequest group(int n) {
        return new SeatRequest(n, SeatPreference.NONE);
    }

    @Nested
    class Window {
        private final SeatAssignmentStrategy strategy = new WindowSeatStrategy();

        @Test
        void emptyPlane_givesFirstWindow() {
            assertEquals(List.of(s(1, 'A')), strategy.assign(new Flight("T", 5), one()));
        }

        @Test
        void skipsTakenWindows() {
            Flight flight = new Flight("T", 5);
            flight.book(List.of(s(1, 'A')));
            assertEquals(List.of(s(1, 'F')), strategy.assign(flight, one()));
        }

        @Test
        void groupOfTwo_getsTwoWindows() {
            assertEquals(List.of(s(1, 'A'), s(1, 'F')), strategy.assign(new Flight("T", 5), group(2)));
        }

        @Test
        void notEnoughWindows_givesEmptyList() {
            assertTrue(strategy.assign(new Flight("T", 1), group(3)).isEmpty());
        }
    }

    @Nested
    class FrontRows {
        private final SeatAssignmentStrategy strategy = new FrontRowsFirstStrategy();

        @Test
        void takesTheFirstFreeSeat() {
            Flight flight = new Flight("T", 5);
            flight.book(List.of(s(1, 'A'), s(1, 'B')));
            assertEquals(List.of(s(1, 'C')), strategy.assign(flight, one()));
        }

        @Test
        void groupOfThree_getsNextThreeInOrder() {
            Flight flight = new Flight("T", 5);
            flight.book(List.of(s(1, 'A'), s(1, 'B')));
            assertEquals(List.of(s(1, 'C'), s(1, 'D'), s(1, 'E')), strategy.assign(flight, group(3)));
        }

        @Test
        void fullPlane_givesEmptyList() {
            Flight flight = new Flight("T", 1);
            flight.book(flight.freeSeats());
            assertTrue(strategy.assign(flight, one()).isEmpty());
        }
    }

    @Nested
    class GroupTogether {
        private final SeatAssignmentStrategy strategy = new GroupTogetherStrategy();

        @Test
        void skipsRunsThatAreTooShort() {
            Flight flight = new Flight("T", 5);
            flight.book(List.of(s(1, 'B'))); // row 1: A | C D E F
            assertEquals(List.of(s(1, 'C'), s(1, 'D'), s(1, 'E')), strategy.assign(flight, group(3)));
        }

        @Test
        void groupOfTwo_usesFirstRunOfTwo() {
            Flight flight = new Flight("T", 5);
            flight.book(List.of(s(1, 'B')));
            assertEquals(List.of(s(1, 'C'), s(1, 'D')), strategy.assign(flight, group(2)));
        }

        @Test
        void movesToNextRowWhenNeeded() {
            Flight flight = new Flight("T", 2);
            flight.book(List.of(s(1, 'B'), s(1, 'E')));
            assertEquals(List.of(s(2, 'A'), s(2, 'B'), s(2, 'C'), s(2, 'D')),
                    strategy.assign(flight, group(4)));
        }

        @Test
        void noRunLongEnough_givesEmptyList() {
            Flight flight = new Flight("T", 1);
            flight.book(List.of(s(1, 'C'), s(1, 'D')));
            assertTrue(strategy.assign(flight, group(4)).isEmpty());
        }
    }

    @Nested
    class EmptyRow {
        private final SeatAssignmentStrategy strategy = new EmptyRowStrategy();

        @Test
        void picksTheEmptiestRow() {
            Flight flight = new Flight("T", 3);
            flight.book(List.of(s(1, 'A'), s(1, 'B'), s(2, 'A')));
            assertEquals(List.of(s(3, 'A')), strategy.assign(flight, one()));
        }

        @Test
        void tie_prefersTheLowerRow() {
            assertEquals(List.of(s(1, 'A')), strategy.assign(new Flight("T", 3), one()));
        }

        @Test
        void groupBiggerThanAnyRowSpace_givesEmptyList() {
            Flight flight = new Flight("T", 2);
            flight.book(List.of(s(1, 'A'), s(2, 'A'), s(2, 'B')));
            assertTrue(strategy.assign(flight, group(6)).isEmpty());
        }
    }
}
