package flight.model;

/** A seat booking request: how many people and what they prefer. */
public record SeatRequest(int groupSize, SeatPreference preference) {

    public static final int MAX_GROUP = Seat.LETTERS.length();

    public SeatRequest {
        if (groupSize < 1 || groupSize > MAX_GROUP) {
            throw new IllegalArgumentException("groupSize must be 1.." + MAX_GROUP);
        }
        if (preference == null) {
            throw new IllegalArgumentException("preference must not be null");
        }
    }
}
