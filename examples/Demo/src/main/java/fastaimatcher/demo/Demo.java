package fastaimatcher.demo;

import fastaimatcher.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" ⚖️ FastAIMatcher — SOX & Compliance Audit Engine");
        System.out.println("=================================================");

        // 1. Define Standard Compliance & Policy Rules (e.g. from Enterprise Security or Change Policy)
        List<Rule> rules = List.of(
                new Rule("SEC-POL-01", Rule.Category.MANDATORY, "Security assessment must be documented", List.of("security assessment"), Double.NaN),
                new Rule("FIN-POL-02", Rule.Category.NUMERIC_LIMIT, "Capital expense limit max 100,000 EUR", List.of(), 100_000.0),
                new Rule("CHG-POL-03", Rule.Category.APPROVAL, "Dual approval (4-eyes principle) mandatory", List.of(), Double.NaN)
        );

        FastAIMatcher matcher = new FastAIMatcher(rules);
        System.out.println("Loaded " + rules.size() + " active compliance rules.");

        // 2. Validate Compliant Target Document (e.g. Change-Ticket / RFC)
        System.out.println("\n--- 1. Evaluating Compliant Document ---");
        TargetDocument validDoc = new TargetDocument(
                "TICKET-8821",
                "Core Database Upgrade",
                "Change request for database upgrade: The security assessment has been fully conducted.",
                Map.of("budget", "65000", "target_env", "prod"),
                List.of("Release Manager", "VP Engineering")
        );

        List<MatchFinding> validResults = matcher.match(validDoc);
        for (MatchFinding f : validResults) {
            System.out.printf("[%s] Rule=%s score=%.2f | %s\n", f.status(), f.ruleId(), f.confidenceScore(), f.explanation());
        }

        // 3. Validate Non-Compliant Target Document (Violations)
        System.out.println("\n--- 2. Evaluating Non-Compliant Document ---");
        TargetDocument rogueDoc = new TargetDocument(
                "TICKET-9942",
                "Emergency Hotfix Bypass",
                "Schneller Fix ohne formelle Dokumentation.",
                Map.of("budget", "180000"),
                List.of()
        );

        List<MatchFinding> rogueResults = matcher.match(rogueDoc);
        for (MatchFinding f : rogueResults) {
            System.out.printf("[%s] Rule=%s score=%.2f | %s\n", f.status(), f.ruleId(), f.confidenceScore(), f.explanation());
        }

        // 4. FastFileFormat Binary Audit Log Serialization (.matchbin)
        byte[] encoded = MatcherCodec.encode(rogueResults);
        System.out.println("\nSerialized .matchbin audit report size: " + encoded.length + " bytes.");

        List<MatchFinding> decoded = MatcherCodec.decode(encoded);
        System.out.println("Decoded " + decoded.size() + " audit findings from binary report.");

        System.out.println("\n✔ FastAIMatcher SOX Compliance Engine Verified Successfully!");
    }
}
