import module java.base;

/**
 * Core DSA logic.
 *  - TreeMap<String, Queue<Student>> : subject groups in sorted order
 *  - Queue (LinkedList)              : round-robin removal from each group
 *  - Greedy + linear search          : choose the first suitable empty seat
 *  - HashMap                         : roll number -> seat location (average O(1))
 */
public final class SeatingOptimizer {

    private final Map<String, SeatLocation> seatIndex = new HashMap<>();
    private boolean generated = false;

    public boolean isGenerated() { return generated; }

    public static int totalCapacity(List<Room> rooms) {
        int total = 0;
        for (Room r : rooms) total += r.capacity();
        return total;
    }

    /** O(n) duplicate-roll validation. */
    public static boolean rollExists(List<Student> students, String rollNo) {
        for (Student s : students) if (s.rollNo().equalsIgnoreCase(rollNo.trim())) return true;
        return false;
    }

    /** Group by subject (TreeMap of queues) and take one student per subject in turn. */
    public static List<Student> roundRobinOrder(List<Student> students) {
        Map<String, Queue<Student>> groups = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Student s : students) {
            groups.computeIfAbsent(s.subject(), _ -> new LinkedList<>()).add(s);
        }
        List<Student> order = new ArrayList<>(students.size());
        while (order.size() < students.size()) {
            for (Queue<Student> q : groups.values()) {
                Student next = q.poll();
                if (next != null) order.add(next);
            }
        }
        return order;
    }

    /** Invalidate the old arrangement (rooms and lookup index). */
    public void invalidate(List<Room> rooms) {
        for (Room r : rooms) r.clear();
        seatIndex.clear();
        generated = false;
    }

    /** Greedy allocation. Checks capacity first and refuses if seats are insufficient. */
    public AllocationResult generate(List<Student> students, List<Room> rooms) {
        invalidate(rooms);
        if (students.isEmpty()) return new AllocationResult.Failure("No students to seat. Add students first.");
        if (rooms.isEmpty()) return new AllocationResult.Failure("No examination rooms. Add a room first.");

        int capacity = totalCapacity(rooms);
        if (students.size() > capacity) {
            return new AllocationResult.Failure("Insufficient capacity: " + students.size()
                    + " students but only " + capacity + " seats.");
        }

        for (Student s : roundRobinOrder(students)) {
            int[] spot = findSeat(rooms, s, true);               // preferred: no same subject left/up
            if (spot == null) spot = findSeat(rooms, s, false);  // fallback: any empty seat
            if (spot == null) {                                  // cannot happen after the capacity check
                invalidate(rooms);
                return new AllocationResult.Failure("Allocation failed unexpectedly.");
            }
            Room room = rooms.get(spot[0]);
            room.place(spot[1], spot[2], s);
            seatIndex.put(s.rollNo(), new SeatLocation(room.name(), spot[1], spot[2]));
        }
        generated = true;
        return new AllocationResult.Success(students.size());
    }

    /** Linear search over all rooms/rows/columns for the first suitable empty seat. */
    private static int[] findSeat(List<Room> rooms, Student s, boolean preferSeparation) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            for (int r = 0; r < room.rows(); r++) {
                for (int c = 0; c < room.cols(); c++) {
                    if (!room.isEmpty(r, c)) continue;
                    if (!preferSeparation || !sameSubjectLeftOrUp(room, r, c, s)) return new int[]{i, r, c};
                }
            }
        }
        return null;
    }

    /** Only the left and upper neighbours are checked (not every adjacent seat). */
    private static boolean sameSubjectLeftOrUp(Room room, int r, int c, Student s) {
        if (c > 0 && same(room.get(r, c - 1), s)) return true;
        return r > 0 && same(room.get(r - 1, c), s);
    }

    private static boolean same(Student other, Student s) {
        return other != null && other.subject().equalsIgnoreCase(s.subject());
    }

    /** Average O(1) lookup through the HashMap index; null if unknown or not generated. */
    public SeatLocation find(String rollNo) {
        return seatIndex.get(rollNo.trim());
    }

    /** Counts horizontally/vertically adjacent same-subject pairs (all four directions covered). */
    public static int adjacentSameSubjectPairs(List<Room> rooms) {
        int count = 0;
        for (Room room : rooms) {
            for (int r = 0; r < room.rows(); r++) {
                for (int c = 0; c < room.cols(); c++) {
                    Student a = room.get(r, c);
                    if (a == null) continue;
                    if (c + 1 < room.cols() && same(room.get(r, c + 1), a)) count++;
                    if (r + 1 < room.rows() && same(room.get(r + 1, c), a)) count++;
                }
            }
        }
        return count;
    }

    /** Prints the room-wise seating chart. O(C). */
    public void printChart(List<Room> rooms) {
        if (!generated) {
            IO.println("No seating arrangement yet. Choose option 3 first.");
            return;
        }
        for (Room room : rooms) {
            IO.println("");
            IO.println("--- " + room.name() + " (" + room.rows() + " x " + room.cols() + ") ---");
            for (int r = 0; r < room.rows(); r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < room.cols(); c++) {
                    Student s = room.get(r, c);
                    String cell = (s == null) ? "[ empty ]" : "[" + s.rollNo() + ":" + s.subject() + "]";
                    line.append(String.format("%-18s", cell));
                }
                IO.println(line.toString().stripTrailing());
            }
        }
        IO.println("");
        IO.println("Adjacent same-subject pairs: " + adjacentSameSubjectPairs(rooms));
    }
}
