package fastaimatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-performance hybrid compliance and rule verification matching engine.
 * Combines symbolic constraint validation with semantic keyword & threshold scoring.
 */
public class FastAIMatcher {

    private final List<Rule> rules;

    public FastAIMatcher(List<Rule> rules) {
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Executes compliance matching against target document.
     *
     * @param document Target document to verify.
     * @return List of rule verification findings.
     */
    public List<MatchFinding> match(TargetDocument document) {
        List<MatchFinding> findings = new ArrayList<>(rules.size());
        String lowerDocText = document.rawText() != null ? document.rawText().toLowerCase() : "";

        for (Rule rule : rules) {
            findings.add(evaluateRule(rule, document, lowerDocText));
        }
        return Collections.unmodifiableList(findings);
    }

    private MatchFinding evaluateRule(Rule rule, TargetDocument document, String lowerDocText) {
        // 1. Keyword Evidence Check
        int matchedKeywords = 0;
        List<String> keywords = rule.requiredKeywords();
        if (keywords != null && !keywords.isEmpty()) {
            for (String kw : keywords) {
                if (lowerDocText.contains(kw.toLowerCase())) {
                    matchedKeywords++;
                }
            }
            if (matchedKeywords < keywords.size()) {
                return new MatchFinding(
                        rule.id(),
                        MatchFinding.Status.MISSING_EVIDENCE,
                        (float) matchedKeywords / keywords.size(),
                        "Missing required condition keywords: " + keywords,
                        ""
                );
            }
        }

        // 2. Numeric Limits Check (e.g. budget, memory, versions)
        if (!Double.isNaN(rule.numericThreshold())) {
            String budgetVal = document.getAttribute("budget");
            if (budgetVal != null) {
                try {
                    double actual = Double.parseDouble(budgetVal);
                    if (actual > rule.numericThreshold()) {
                        return new MatchFinding(
                                rule.id(),
                                MatchFinding.Status.VIOLATION,
                                0.99f,
                                String.format("Exceeded numeric threshold: actual %.2f > limit %.2f", actual, rule.numericThreshold()),
                                "budget=" + actual
                        );
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // 3. Approval / Four-Eyes-Principle Check
        if (rule.category() == Rule.Category.APPROVAL) {
            if (document.approvers() == null || document.approvers().isEmpty()) {
                return new MatchFinding(
                        rule.id(),
                        MatchFinding.Status.VIOLATION,
                        1.0f,
                        "Mandatory approval signature is missing",
                        "approvers=[]"
                );
            }
        }

        return new MatchFinding(
                rule.id(),
                MatchFinding.Status.COMPLIANT,
                0.98f,
                "Rule conditions verified and satisfied",
                document.title()
        );
    }
}
