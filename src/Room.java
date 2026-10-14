import module java.base;   // Java 25 (JEP 511): imports every package exported by java.base

/** An examination room modelled as a 2D array of seats. */
public final class Room {
    private final String name;
    private final int rows;
    private final int cols;
    private final Student[][] seats;   // seats[row][col]; null means empty

    public Room(String name, int rows, int cols) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Room name is required.");
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("Rows and columns must be positive.");
        this.name = name.trim();
        this.rows = rows;
        this.cols = cols;
        this.seats = new Student[rows][cols];
    }

    // [J25-FLEXIBLE-CTOR-START]
    /** Convenience constructor, e.g. new Room("Room A", "2x3"). */
    public Room(String name, String size) {
        // Java 25 (JEP 513, flexible constructor bodies): statements may run before this(...)
        String[] parts = size.toLowerCase().split("x");
        if (parts.length != 2) throw new IllegalArgumentException("Size must look like 2x3.");
        this(name, Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
    }
    // [J25-FLEXIBLE-CTOR-END]

    public String name() { return name; }
    public int rows() { return rows; }
    public int cols() { return cols; }
    public int capacity() { return rows * cols; }

    public Student get(int r, int c) { return seats[r][c]; }
    public boolean isEmpty(int r, int c) { return seats[r][c] == null; }
    public void place(int r, int c, Student s) { seats[r][c] = s; }

    /** Empties every seat (used when the old arrangement is invalidated). */
    public void clear() {
        for (Student[] row : seats) Arrays.fill(row, null);
    }
}
