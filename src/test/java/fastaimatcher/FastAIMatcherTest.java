package fastaimatcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FastAIMatcherTest {

    @Test
    public void testRuleComplianceVerification() {
        Rule r1 = new Rule("BHO-01", Rule.Category.MANDATORY, "Economic interest must be demonstrated", List.of("wirtschaftliches interesse"), Double.NaN);
        Rule r2 = new Rule("SOX-BUDGET", Rule.Category.NUMERIC_LIMIT, "Budget must not exceed 100,000 EUR", List.of(), 100_000.0);
        Rule r3 = new Rule("SOX-APPROVAL", Rule.Category.APPROVAL, "Four-eyes principle approval required", List.of(), Double.NaN);

        FastAIMatcher matcher = new FastAIMatcher(List.of(r1, r2, r3));

        TargetDocument docValid = new TargetDocument(
                "doc-101",
                "Projektantrag Cloud-Migration",
                "Der Antrag begründet das wirtschaftliches interesse des Bundes umfassend.",
                Map.of("budget", "75000"),
                List.of("Dr. Müller", "Hr. Schmidt")
        );

        List<MatchFinding> findings = matcher.match(docValid);
        assertEquals(3, findings.size());
        assertEquals(MatchFinding.Status.COMPLIANT, findings.get(0).status());
        assertEquals(MatchFinding.Status.COMPLIANT, findings.get(1).status());
        assertEquals(MatchFinding.Status.COMPLIANT, findings.get(2).status());

        // Test Violation: Over Budget & Missing Keyword
        TargetDocument docInvalid = new TargetDocument(
                "doc-102",
                "Projektantrag Kulturförderung",
                "Förderung von Kunst und Festspielen.",
                Map.of("budget", "150000"),
                List.of()
        );

        List<MatchFinding> invalidFindings = matcher.match(docInvalid);
        assertEquals(MatchFinding.Status.MISSING_EVIDENCE, invalidFindings.get(0).status());
        assertEquals(MatchFinding.Status.VIOLATION, invalidFindings.get(1).status());
        assertEquals(MatchFinding.Status.VIOLATION, invalidFindings.get(2).status());
    }

    @Test
    public void testMatchCodecSerialization(@TempDir Path tempDir) throws IOException {
        List<MatchFinding> findings = List.of(
                new MatchFinding("R1", MatchFinding.Status.COMPLIANT, 0.95f, "Verified", "snippet 1"),
                new MatchFinding("R2", MatchFinding.Status.VIOLATION, 0.99f, "Budget exceeded", "budget=150k")
        );

        byte[] encoded = MatcherCodec.encode(findings);
        assertNotNull(encoded);
        assertTrue(encoded.length >= 12);

        List<MatchFinding> decoded = MatcherCodec.decode(encoded);
        assertEquals(2, decoded.size());
        assertEquals("R1", decoded.get(0).ruleId());
        assertEquals(MatchFinding.Status.COMPLIANT, decoded.get(0).status());

        Path file = tempDir.resolve("audit.matchbin");
        MatcherCodec.writeToFile(file, findings);
        assertTrue(file.toFile().exists());

        List<MatchFinding> fromDisk = MatcherCodec.readFromFile(file);
        assertEquals(2, fromDisk.size());
    }
}
