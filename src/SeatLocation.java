/** Where a student sits. Row and column are stored 0-based and shown 1-based. */
public record SeatLocation(String roomName, int row, int col) {
    @Override
    public String toString() {
        return roomName + ", Row " + (row + 1) + ", Column " + (col + 1);
    }
}
