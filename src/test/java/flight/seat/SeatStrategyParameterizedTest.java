package flight.seat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import flight.model.Flight;
import flight.model.Seat;
import flight.model.SeatPreference;
import flight.model.SeatRequest;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** The same contract is checked on EVERY seat strategy and several group sizes. */
class SeatStrategyParameterizedTest {

    static Stream<Arguments> everyStrategyWithEveryGroupSize() {
        List<SeatAssignmentStrategy> strategies = List.of(
                new WindowSeatStrategy(),
                new FrontRowsFirstStrategy(),
                new GroupTogetherStrategy(),
                new EmptyRowStrategy());
        return strategies.stream().flatMap(strategy ->
                Stream.of(1, 2, 3, 4).map(size -> Arguments.of(strategy, size)));
    }

    private static Flight partlyFilledFlight() {
        Flight flight = new Flight("T", 6);
        flight.book(List.of(
                new Seat(1, 'A'), new Seat(1, 'B'), new Seat(1, 'C'),
                new Seat(2, 'A'), new Seat(2, 'F'),
                new Seat(3, 'C'), new Seat(3, 'D')));
        return flight;
    }

    @ParameterizedTest(name = "{0} / group of {1}")
    @MethodSource("everyStrategyWithEveryGroupSize")
    void result_isEmptyOrExactlyTheRequestedFreeDistinctSeats(SeatAssignmentStrategy strategy, int size) {
        Flight flight = partlyFilledFlight();
        List<Seat> seats = strategy.assign(flight, new SeatRequest(size, SeatPreference.NONE));

        assertTrue(seats.isEmpty() || seats.size() == size, "wrong number of seats");
        assertTrue(seats.stream().allMatch(flight::isFree), "an assigned seat is not free");
        assertEquals(seats.size(), new HashSet<>(seats).size(), "duplicate seats returned");
    }

    @ParameterizedTest(name = "{0} / group of {1}")
    @MethodSource("everyStrategyWithEveryGroupSize")
    void assign_doesNotBookAnything(SeatAssignmentStrategy strategy, int size) {
        Flight flight = partlyFilledFlight();
        int before = flight.occupancyPercent();
        strategy.assign(flight, new SeatRequest(size, SeatPreference.NONE));
        assertEquals(before, flight.occupancyPercent());
    }
}
