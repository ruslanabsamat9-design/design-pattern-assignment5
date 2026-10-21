package flight.model;

/**
 * Everything the fare calculation needs to know about one ticket request.
 */
public record FlightRequest(
        String from,
        String to,
        double distanceKm,
        int daysBeforeDeparture,
        int occupancyPercent,
        boolean international) {

    public FlightRequest {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("distanceKm must be >= 0");
        }
        if (daysBeforeDeparture < 0) {
            throw new IllegalArgumentException("daysBeforeDeparture must be >= 0");
        }
        if (occupancyPercent < 0 || occupancyPercent > 100) {
            throw new IllegalArgumentException("occupancyPercent must be 0..100");
        }
    }
}
