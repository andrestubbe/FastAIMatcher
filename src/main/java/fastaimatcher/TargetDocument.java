package fastaimatcher;

import java.util.List;
import java.util.Map;

/**
 * Normalized document representation extracted from target artifacts (Antrag, Ticket, Deployment Log, HR record).
 *
 * @param docId Unique document identifier.
 * @param title Document title or summary.
 * @param rawText Full text body or extracted paragraphs.
 * @param keyValues Extracted structured key-value attributes (e.g. "budget" -> "50000", "version" -> "1.2.4").
 * @param approvers List of recorded approvers / signatures.
 */
public record TargetDocument(
        String docId,
        String title,
        String rawText,
        Map<String, String> keyValues,
        List<String> approvers
) {
    public String getAttribute(String key) {
        return keyValues != null ? keyValues.get(key) : null;
    }
}
