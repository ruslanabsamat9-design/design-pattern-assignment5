package flight.fare;

import flight.model.FlightRequest;

/**
 * Strategy interface: one way of turning a flight request into a ticket price.
 */
public interface FareStrategy {

    /** Ticket price in KZT, rounded to 2 decimals. */
    double calculate(FlightRequest request);

    /** Human readable name, used by the demo. */
    default String name() {
        return getClass().getSimpleName();
    }
}
