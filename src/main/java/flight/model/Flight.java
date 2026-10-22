package flight.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A flight with a seat map of {@code rows} rows and 6 seats (A-F) per row. */
public class Flight {

    private final String number;
    private final int rows;
    private final Set<Seat> occupied = new HashSet<>();

    public Flight(String number, int rows) {
        if (rows < 1) {
            throw new IllegalArgumentException("rows must be >= 1");
        }
        this.number = number;
        this.rows = rows;
    }

    public String getNumber() {
        return number;
    }

    public int getRows() {
        return rows;
    }

    public int totalSeats() {
        return rows * Seat.LETTERS.length();
    }

    public boolean isFree(Seat seat) {
        return seat.row() <= rows && !occupied.contains(seat);
    }

    /** Marks seats as taken. Fails (and changes nothing) if any seat is invalid or taken. */
    public void book(Collection<Seat> seats) {
        for (Seat seat : seats) {
            if (!isFree(seat)) {
                throw new IllegalStateException("Seat not available: " + seat);
            }
        }
        occupied.addAll(seats);
    }

    /** All free seats in row order (1A, 1B, ... 2A, ...). */
    public List<Seat> freeSeats() {
        List<Seat> result = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            result.addAll(freeSeatsInRow(row));
        }
        return result;
    }

    public List<Seat> freeSeatsInRow(int row) {
        List<Seat> result = new ArrayList<>();
        for (char letter : Seat.LETTERS.toCharArray()) {
            Seat seat = new Seat(row, letter);
            if (isFree(seat)) {
                result.add(seat);
            }
        }
        return result;
    }

    public int occupancyPercent() {
        return occupied.size() * 100 / totalSeats();
    }
}
