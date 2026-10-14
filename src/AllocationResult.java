/** Sealed result type: allocation either succeeds or fails with a reason. */
public sealed interface AllocationResult permits AllocationResult.Success, AllocationResult.Failure {
    record Success(int seated) implements AllocationResult { }
    record Failure(String reason) implements AllocationResult { }
}
