package flight.model;

/** One seat: row number and letter A-F. Window seats are A and F. */
public record Seat(int row, char letter) implements Comparable<Seat> {

    public static final String LETTERS = "ABCDEF";

    public Seat {
        if (row < 1) {
            throw new IllegalArgumentException("row must be >= 1");
        }
        if (LETTERS.indexOf(letter) < 0) {
            throw new IllegalArgumentException("letter must be one of " + LETTERS);
        }
    }

    public boolean isWindow() {
        return letter == 'A' || letter == 'F';
    }

    @Override
    public int compareTo(Seat other) {
        int byRow = Integer.compare(row, other.row);
        return byRow != 0 ? byRow : Character.compare(letter, other.letter);
    }

    @Override
    public String toString() {
        return row + String.valueOf(letter);
    }
}
