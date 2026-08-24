package fastaimatcher;

/**
 * Result finding of matching a single compliance Rule against a TargetDocument.
 *
 * @param ruleId ID of verified rule.
 * @param status Finding status (COMPLIANT, VIOLATION, MISSING_EVIDENCE, WARNING).
 * @param confidenceScore Composite hybrid match score (0.0 to 1.0).
 * @param explanation Human-readable audit explanation.
 * @param evidenceSnippet Exact text evidence snippet from target document.
 */
public record MatchFinding(
        String ruleId,
        Status status,
        float confidenceScore,
        String explanation,
        String evidenceSnippet
) {
    public enum Status {
        COMPLIANT,
        VIOLATION,
        MISSING_EVIDENCE,
        WARNING
    }

    public boolean isViolated() {
        return status == Status.VIOLATION || status == Status.MISSING_EVIDENCE;
    }
}
