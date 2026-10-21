package flight.fare;

/** Selects the pricing variant inside the one big FareCalculator (the "before" design). */
public enum FareType {
    DISTANCE,
    DEMAND,
    LAST_MINUTE,
    INTERNATIONAL
}
