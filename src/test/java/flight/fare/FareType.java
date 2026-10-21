package flight.fare;

/** Test-only enum: names the fare variants so the shared behaviour test can address all of them. */
public enum FareType {
    DISTANCE,
    DEMAND,
    LAST_MINUTE,
    INTERNATIONAL
}
