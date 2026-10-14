/** Immutable student record: roll number, name and the subject being examined. */
public record Student(String rollNo, String name, String subject) {
    public Student {
        if (rollNo == null || rollNo.isBlank()) throw new IllegalArgumentException("Roll number is required.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("Subject is required.");
        rollNo = rollNo.trim();
        name = name.trim();
        subject = subject.trim();
    }
}
