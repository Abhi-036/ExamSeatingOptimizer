# Dynamic Exam Seating Optimizer (Java 25 LTS)

Console-based DSA project by **Abhisek Das** 

## Requirements
JDK 25 (LTS) and VS Code with "Extension Pack for Java". Check with `javac -version`.

## Run (terminal inside this folder)
```
javac -d out src/*.java
java -cp out Main --sample    # preloads Room A (2x3), Room B (2x2) and 6 students
java -cp out Main             # start empty
java -cp out SeatingTests     # run the tests
```
Demo order: 3 (generate), 4 (chart), 5 then 105 (search), 6 then 106 (remove), 7 (exit).

## Structure
```
src/Main.java              menu; instance main method + IO class
src/Student.java           record
src/Room.java              2D array of seats; flexible constructor body
src/SeatLocation.java      record
src/AllocationResult.java  sealed interface (Success / Failure)
src/SeatingOptimizer.java  TreeMap + Queue, round-robin, greedy search, HashMap index
src/SeatingTests.java      tests for the six PPT cases
```

## Java 25 features used
| Feature | Where |
|---|---|
| Instance main method, no `public static` (JEP 512) | `Main.main`, `SeatingTests.main` |
| `IO.println` / `IO.readln` in java.lang (JEP 512) | all console I/O |
| Module import `import module java.base;` (JEP 511) | every file that needs java.util |
| Flexible constructor bodies (JEP 513) | `Room(String, String)` |
| Unnamed variable `_` | `catch (NumberFormatException _)`, `computeIfAbsent(s, _ -> ...)` |
| Sealed interface + record-pattern switch | `AllocationResult`, `Main.generate` |
| Records, text blocks, `getFirst()` | `Student`, `MENU`, tests |

## DSA used
2D array (rooms), ArrayList (records), HashMap (roll -> seat, average O(1)), TreeMap (sorted subject groups),
Queue/LinkedList (round-robin), greedy + linear search (seat allocation), regeneration after changes.
HashSet is only a proposed extension. Only left and upper neighbours are checked; the result is not a guaranteed global optimum.

Complexity: add student O(n); lookup O(1) average; allocation up to O(S x C); display O(C); storage O(S + C).
