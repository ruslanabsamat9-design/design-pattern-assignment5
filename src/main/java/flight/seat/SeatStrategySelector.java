package flight.seat;

import flight.model.SeatRequest;

/**
 * Chooses a seat strategy automatically from the request.
 * This is the ONLY place where if / switch on the situation is allowed.
 *
 * Rules (first match wins):
 *   1. more than one passenger      -> GroupTogetherStrategy
 *   2. preference WINDOW            -> WindowSeatStrategy
 *   3. preference MORE_SPACE        -> EmptyRowStrategy
 *   4. everything else              -> FrontRowsFirstStrategy
 */
public class SeatStrategySelector {

    private final SeatAssignmentStrategy together = new GroupTogetherStrategy();
    private final SeatAssignmentStrategy window = new WindowSeatStrategy();
    private final SeatAssignmentStrategy emptyRow = new EmptyRowStrategy();
    private final SeatAssignmentStrategy frontRows = new FrontRowsFirstStrategy();

    public SeatAssignmentStrategy select(SeatRequest request) {
        if (request.groupSize() > 1) {
            return together;
        }
        switch (request.preference()) {
            case WINDOW:
                return window;
            case MORE_SPACE:
                return emptyRow;
            default:
                return frontRows;
        }
    }
}
