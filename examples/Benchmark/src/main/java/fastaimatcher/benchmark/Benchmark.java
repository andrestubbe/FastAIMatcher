package fastaimatcher.benchmark;

import fastaimatcher.*;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private FastAIMatcher matcher;
    private TargetDocument sampleDoc;
    private List<MatchFinding> sampleFindings;
    private byte[] sampleBinary;

    @Setup
    public void setup() {
        List<Rule> rules = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            rules.add(new Rule(
                    "RULE-" + i,
                    (i % 3 == 0) ? Rule.Category.APPROVAL : Rule.Category.MANDATORY,
                    "Condition description for rule " + i,
                    List.of("bund", "wirtschaftlich", "compliance"),
                    100_000.0 + (i * 1000)
            ));
        }
        matcher = new FastAIMatcher(rules);

        sampleDoc = new TargetDocument(
                "doc-benchmark",
                "Projektantrag IT Infrastructure Upgrade",
                "Ausführlicher text mit bund und wirtschaftlich orientierter compliance ausrichtung.",
                Map.of("budget", "85000", "version", "2.1.0"),
                List.of("Lead Architect", "CISO")
        );

        sampleFindings = matcher.match(sampleDoc);
        sampleBinary = MatcherCodec.encode(sampleFindings);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<MatchFinding> benchmarkMatch50RulesAgainstDocument() {
        return matcher.match(sampleDoc);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkEncodeFindings() {
        return MatcherCodec.encode(sampleFindings);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<MatchFinding> benchmarkDecodeFindings() {
        return MatcherCodec.decode(sampleBinary);
    }
}
