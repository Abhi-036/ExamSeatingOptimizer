import module java.base;

/**
 * Console menu for the Dynamic Exam Seating Optimizer.
 * Java 25 features used: instance main method + java.lang.IO (JEP 512),
 * module imports (JEP 511), flexible constructor bodies (JEP 513, in Room),
 * unnamed variables (_), sealed types with record-pattern switch, text blocks.
 * Run with --sample to preload demo data.
 */
class Main {
    private static final String MENU = """

            === Exam Seating Optimizer ===
            1. Add Student
            2. Add Examination Room
            3. Generate Seating Arrangement
            4. Display Seating Chart
            5. Search Student Seat
            6. Remove Student
            7. Exit
            """;

    private final List<Student> students = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();
    private final SeatingOptimizer optimizer = new SeatingOptimizer();

    // Java 25: no "public static" and no class boilerplate needed for the launcher
    void main(String[] args) {
        if (args.length > 0 && args[0].equals("--sample")) loadSample();
        while (true) {
            IO.print(MENU);
            String choice = IO.readln("Enter choice: ");
            if (choice == null) return;   // end of input
            switch (choice.trim()) {
                case "1" -> addStudent();
                case "2" -> addRoom();
                case "3" -> generate();
                case "4" -> optimizer.printChart(rooms);
                case "5" -> searchSeat();
                case "6" -> removeStudent();
                case "7" -> { IO.println("Goodbye."); return; }
                default -> IO.println("Invalid choice. Enter 1-7.");
            }
        }
    }

    private String ask(String prompt) {
        String s = IO.readln(prompt);
        return s == null ? "" : s.trim();
    }

    private void addStudent() {
        String roll = ask("Roll number: ");
        if (SeatingOptimizer.rollExists(students, roll)) {
            IO.println("Error: roll number already exists.");
            return;
        }
        try {
            students.add(new Student(roll, ask("Name: "), ask("Subject: ")));
            if (optimizer.isGenerated()) optimizer.invalidate(rooms);
            IO.println("Student added. Regenerate seating (option 3) to apply.");
        } catch (IllegalArgumentException e) {
            IO.println("Error: " + e.getMessage());
        }
    }

    private void addRoom() {
        try {
            String name = ask("Room name: ");
            for (Room r : rooms) {
                if (r.name().equalsIgnoreCase(name)) { IO.println("Error: room already exists."); return; }
            }
            int rows = Integer.parseInt(ask("Rows: "));
            int cols = Integer.parseInt(ask("Columns: "));
            rooms.add(new Room(name, rows, cols));
            if (optimizer.isGenerated()) optimizer.invalidate(rooms);
            IO.println("Room added. Total capacity: " + SeatingOptimizer.totalCapacity(rooms));
        } catch (NumberFormatException _) {   // Java 22+: unnamed variable
            IO.println("Error: rows and columns must be whole numbers.");
        } catch (IllegalArgumentException e) {
            IO.println("Error: " + e.getMessage());
        }
    }

    private void generate() {
        // Pattern-matching switch over the sealed result with record deconstruction
        switch (optimizer.generate(students, rooms)) {
            case AllocationResult.Success(int seated) -> {
                IO.println("Seating generated for " + seated + " students.");
                optimizer.printChart(rooms);
            }
            case AllocationResult.Failure(String reason) -> IO.println("Error: " + reason);
        }
    }

    private void searchSeat() {
        if (!optimizer.isGenerated()) { IO.println("Generate the seating first (option 3)."); return; }
        SeatLocation loc = optimizer.find(ask("Enter roll number: "));
        IO.println(loc == null ? "Roll number not found." : "Seat: " + loc);
    }

    private void removeStudent() {
        String roll = ask("Roll number to remove: ");
        if (!students.removeIf(s -> s.rollNo().equalsIgnoreCase(roll))) {
            IO.println("Roll number not found.");
            return;
        }
        IO.println("Student removed.");
        if (optimizer.isGenerated()) {   // dynamic reallocation
            IO.println("Regenerating seating...");
            generate();
        }
    }

    private void loadSample() {
        rooms.add(new Room("Room A", "2x3"));
        rooms.add(new Room("Room B", "2x2"));
        String[][] data = {{"101", "Aman", "Java"}, {"102", "Bina", "DBMS"}, {"103", "Chitra", "OS"},
                           {"104", "Deepak", "Java"}, {"105", "Esha", "DBMS"}, {"106", "Farhan", "OS"}};
        for (String[] d : data) students.add(new Student(d[0], d[1], d[2]));
        IO.println("Sample data loaded: 2 rooms, 6 students.");
    }
}
