import module java.base;

/** Dependency-free tests for the six cases on the Testing slide. Run: java -cp out SeatingTests */
class SeatingTests {
    private int passed = 0, failed = 0;

    private void check(String name, boolean ok) {
        IO.println((ok ? "PASS  " : "FAIL  ") + name);
        if (ok) passed++; else failed++;
    }

    private static List<Student> sampleStudents() {
        List<Student> l = new ArrayList<>();
        String[][] d = {{"101", "Aman", "Java"}, {"102", "Bina", "DBMS"}, {"103", "Chitra", "OS"},
                        {"104", "Deepak", "Java"}, {"105", "Esha", "DBMS"}, {"106", "Farhan", "OS"}};
        for (String[] x : d) l.add(new Student(x[0], x[1], x[2]));
        return l;
    }

    private static List<Room> sampleRooms() {
        return new ArrayList<>(List.of(new Room("Room A", 2, 3), new Room("Room B", 2, 2)));
    }

    void main() {
        // 1. Successful allocation when capacity is sufficient
        var o = new SeatingOptimizer();
        List<Student> st = sampleStudents();
        List<Room> rm = sampleRooms();
        check("1. Allocation succeeds with sufficient capacity", o.generate(st, rm) instanceof AllocationResult.Success);

        // 2. Rejection when capacity is insufficient
        var o2 = new SeatingOptimizer();
        List<Room> small = new ArrayList<>(List.of(new Room("Tiny", 1, 2)));
        check("2. Insufficient capacity is rejected",
                o2.generate(sampleStudents(), small) instanceof AllocationResult.Failure && !o2.isGenerated());

        // 3. Duplicate roll number validation
        check("3. Duplicate roll number detected", SeatingOptimizer.rollExists(st, "101")
                && !SeatingOptimizer.rollExists(st, "999"));

        // 4. Successful roll-number lookup
        SeatLocation loc = o.find("105");
        check("4. Known roll 105 -> Room A, Row 2, Column 2",
                loc != null && loc.toString().equals("Room A, Row 2, Column 2"));

        // 5. Unknown roll number
        check("5. Unknown roll number returns null", o.find("999") == null);

        // 6. Removing a student and regenerating
        st.removeIf(s -> s.rollNo().equals("106"));
        boolean ok = o.generate(st, rm) instanceof AllocationResult.Success && o.find("106") == null && o.find("101") != null;
        check("6. Remove student and regenerate", ok);

        // Extra: sample layout has no adjacent same-subject pairs; "2x3" constructor works
        var o3 = new SeatingOptimizer();
        List<Room> rm3 = new ArrayList<>(List.of(new Room("Room A", "2x3"), new Room("Room B", "2x2")));
        o3.generate(sampleStudents(), rm3);
        check("Extra: sample layout has 0 adjacent same-subject pairs, 2x3 constructor works",
                SeatingOptimizer.adjacentSameSubjectPairs(rm3) == 0 && rm3.getFirst().capacity() == 6);

        IO.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
