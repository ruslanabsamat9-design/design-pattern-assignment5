package flight;

import flight.data.ExchangeRateProvider;
import flight.data.SimulatedExchangeRateProvider;
import flight.fare.DemandFareStrategy;
import flight.fare.DistanceFareStrategy;
import flight.fare.FareCalculator;
import flight.fare.FareStrategy;
import flight.fare.FareStrategySelector;
import flight.fare.InternationalFareStrategy;
import flight.fare.LastMinuteFareStrategy;
import flight.model.Flight;
import flight.model.FlightRequest;
import flight.model.Seat;
import flight.model.SeatPreference;
import flight.model.SeatRequest;
import flight.seat.EmptyRowStrategy;
import flight.seat.FrontRowsFirstStrategy;
import flight.seat.GroupTogetherStrategy;
import flight.seat.SeatAssigner;
import flight.seat.SeatAssignmentStrategy;
import flight.seat.SeatStrategySelector;
import flight.seat.WindowSeatStrategy;
import java.util.List;

/** Console demo of both strategy families, both selectors and runtime switching. */
public class Main {

    public static void main(String[] args) {
        ExchangeRateProvider rates = new SimulatedExchangeRateProvider();

        fareDemo(rates);
        seatDemo();
    }

    // =====================================================================
    //  FAMILY 1: FARE
    // =====================================================================
    private static void fareDemo(ExchangeRateProvider rates) {
        List<FareStrategy> all = List.of(
                new DistanceFareStrategy(),
                new DemandFareStrategy(),
                new LastMinuteFareStrategy(),
                new InternationalFareStrategy(rates));

        header("1. ONE request, EVERY fare strategy (manual choice)");
        FlightRequest istanbul = new FlightRequest("NQZ", "IST", 3200, 5, 65, true);
        System.out.println("Request: " + istanbul);
        FareCalculator calculator = new FareCalculator(all.get(0));
        for (FareStrategy strategy : all) {
            calculator.setStrategy(strategy);               // runtime switch
            System.out.printf("  %-26s %,12.2f KZT%n", strategy.name(), calculator.calculate(istanbul));
        }

        header("2. Manual choice vs. automatic choice (FareStrategySelector)");
        FareStrategySelector selector = new FareStrategySelector(rates);
        FareStrategy manual = new DistanceFareStrategy();
        List<String> labels = List.of(
                "Almaty, in a month, empty plane",
                "Almaty, tomorrow",
                "Almaty, in 10 days, plane 90% full",
                "Istanbul, in 3 weeks, empty plane");
        List<FlightRequest> requests = List.of(
                new FlightRequest("NQZ", "ALA", 1000, 30, 20, false),
                new FlightRequest("NQZ", "ALA", 1000, 1, 20, false),
                new FlightRequest("NQZ", "ALA", 1000, 10, 90, false),
                new FlightRequest("NQZ", "IST", 3200, 21, 20, true));
        System.out.printf("  %-36s %-14s %-26s %s%n", "Situation", "Manual (KZT)", "Automatic strategy", "Automatic (KZT)");
        for (int i = 0; i < requests.size(); i++) {
            FlightRequest r = requests.get(i);
            FareStrategy chosen = selector.select(r);
            System.out.printf("  %-36s %,12.2f   %-26s %,12.2f%n",
                    labels.get(i), manual.calculate(r), chosen.name(), chosen.calculate(r));
        }

        header("3. Runtime switching while the plane fills up");
        FareCalculator live = new FareCalculator(selector.select(requests.get(0)));
        for (int occupancy : new int[] {20, 55, 85, 95}) {
            FlightRequest r = new FlightRequest("NQZ", "ALA", 1000, 10, occupancy, false);
            live.setStrategy(selector.select(r));           // runtime switch
            System.out.printf("  plane %3d%% full -> %-22s %,10.2f KZT%n",
                    occupancy, live.getStrategy().name(), live.calculate(r));
        }
    }

    // =====================================================================
    //  FAMILY 2: SEATS
    // =====================================================================
    private static void seatDemo() {
        List<SeatAssignmentStrategy> all = List.of(
                new WindowSeatStrategy(),
                new FrontRowsFirstStrategy(),
                new GroupTogetherStrategy(),
                new EmptyRowStrategy());

        header("4. ONE seat request, EVERY seat strategy (manual choice)");
        Flight flight = demoFlight();
        System.out.println("Flight " + flight.getNumber() + ", " + flight.occupancyPercent() + "% full");
        SeatAssigner assigner = new SeatAssigner(all.get(0));
        for (int size : new int[] {1, 2}) {
            SeatRequest request = new SeatRequest(size, SeatPreference.NONE);
            System.out.println("  Group of " + size + ":");
            for (SeatAssignmentStrategy strategy : all) {
                assigner.setStrategy(strategy);             // runtime switch
                System.out.printf("    %-24s -> %s%n", strategy.name(), show(assigner.assign(flight, request)));
            }
        }

        header("5. Automatic choice (SeatStrategySelector)");
        SeatStrategySelector selector = new SeatStrategySelector();
        List<SeatRequest> requests = List.of(
                new SeatRequest(3, SeatPreference.NONE),
                new SeatRequest(1, SeatPreference.WINDOW),
                new SeatRequest(1, SeatPreference.MORE_SPACE),
                new SeatRequest(1, SeatPreference.NONE));
        for (SeatRequest request : requests) {
            SeatAssignmentStrategy chosen = selector.select(request);
            System.out.printf("  %-44s -> %-22s %s%n",
                    request, chosen.name(), show(chosen.assign(flight, request)));
        }

        header("6. Runtime switching while passengers book");
        Flight small = new Flight("KC-202", 2);
        SeatAssigner live = new SeatAssigner(new WindowSeatStrategy());
        SeatRequest single = new SeatRequest(1, SeatPreference.NONE);
        for (int i = 1; i <= 4; i++) {
            if (i == 3) {
                live.setStrategy(new FrontRowsFirstStrategy()); // windows run out: switch
            }
            List<Seat> seats = live.assign(small, single);
            System.out.printf("  passenger %d: %-22s -> %s%n", i, live.getStrategy().name(), show(seats));
            small.book(seats);
        }
    }

    /** A flight where rows 1-2 are mostly taken, so strategies give different answers. */
    private static Flight demoFlight() {
        Flight flight = new Flight("KC-101", 10);
        flight.book(List.of(
                new Seat(1, 'A'), new Seat(1, 'B'), new Seat(1, 'C'),
                new Seat(1, 'D'), new Seat(1, 'E'), new Seat(1, 'F'),
                new Seat(2, 'A'), new Seat(2, 'C'), new Seat(2, 'F'),
                new Seat(3, 'B'), new Seat(3, 'E'),
                new Seat(4, 'A'), new Seat(4, 'D')));
        return flight;
    }

    private static String show(List<Seat> seats) {
        return seats.isEmpty() ? "(no seats found)" : seats.toString();
    }

    private static void header(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }
}
