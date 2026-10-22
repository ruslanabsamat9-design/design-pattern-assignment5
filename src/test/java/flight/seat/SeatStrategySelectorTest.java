package flight.seat;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import flight.model.SeatPreference;
import flight.model.SeatRequest;
import org.junit.jupiter.api.Test;

/** One test per Selector rule, plus rule order. */
class SeatStrategySelectorTest {

    private final SeatStrategySelector selector = new SeatStrategySelector();

    @Test
    void rule1_group_picksGroupTogether() {
        assertInstanceOf(GroupTogetherStrategy.class,
                selector.select(new SeatRequest(3, SeatPreference.NONE)));
    }

    @Test
    void rule2_windowWish_picksWindow() {
        assertInstanceOf(WindowSeatStrategy.class,
                selector.select(new SeatRequest(1, SeatPreference.WINDOW)));
    }

    @Test
    void rule3_moreSpaceWish_picksEmptyRow() {
        assertInstanceOf(EmptyRowStrategy.class,
                selector.select(new SeatRequest(1, SeatPreference.MORE_SPACE)));
    }

    @Test
    void rule4_noWish_picksFrontRows() {
        assertInstanceOf(FrontRowsFirstStrategy.class,
                selector.select(new SeatRequest(1, SeatPreference.NONE)));
    }

    @Test
    void groupBeatsPreference() {
        assertInstanceOf(GroupTogetherStrategy.class,
                selector.select(new SeatRequest(2, SeatPreference.WINDOW)));
    }
}
