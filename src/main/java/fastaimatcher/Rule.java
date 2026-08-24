package fastaimatcher;

import java.util.List;

/**
 * Normalized compliance / policy rule extracted from reference documents (e.g. BHO.pdf, HR-Matrix, Change Policy).
 *
 * @param id Unique rule identifier (e.g. "SOX-CHG-01", "BHO-SEC-14").
 * @param category Rule category (MANDATORY, RESTRICTED, NUMERIC_LIMIT, APPROVAL, GENERAL).
 * @param ruleText Full textual description of the condition.
 * @param requiredKeywords Specific keywords or tokens that must be present.
 * @param numericThreshold Numeric upper/lower threshold (if applicable, or Double.NaN).
 */
public record Rule(
        String id,
        Category category,
        String ruleText,
        List<String> requiredKeywords,
        double numericThreshold
) {
    public enum Category {
        MANDATORY,
        RESTRICTED,
        NUMERIC_LIMIT,
        APPROVAL,
        GENERAL
    }
}
