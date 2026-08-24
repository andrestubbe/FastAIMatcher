# FastAIMatcher 0.1.0 [ALPHA] — Automated SOX Compliance & Hybrid Rule Matching Engine

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastAIMatcher/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastAIMatcher)

---

**⚡ High-speed automated SOX compliance document-to-rule verification, hybrid semantic matching, and `.matchbin` audit reporting engine for Java.**

**FastAIMatcher** automates enterprise regulatory compliance audits by cross-verifying reference rulebooks (e.g. **BHO.pdf**, SOX policies, HR authorization matrices) against target operational artifacts (**Change-Tickets**, **Deployment Logs**, **Project Applications**, **Access Grants**) in microseconds without expensive manual inspection.

---

## Quick Start

```java
import fastaimatcher.*;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        // 1. Define compliance & policy rules
        List<Rule> rules = List.of(
                new Rule("SOX-BHO-01", Rule.Category.MANDATORY, "Economic interest must be documented", List.of("wirtschaftliches interesse"), Double.NaN),
                new Rule("SOX-FIN-02", Rule.Category.NUMERIC_LIMIT, "Capital expense limit max 100,000 EUR", List.of(), 100_000.0),
                new Rule("SOX-SEC-03", Rule.Category.APPROVAL, "Dual approval (4-eyes principle) mandatory", List.of(), Double.NaN)
        );

        FastAIMatcher matcher = new FastAIMatcher(rules);

        // 2. Validate operational target document (e.g. Jira Ticket or Antrag)
        TargetDocument doc = new TargetDocument(
                "TICKET-8821",
                "Database Migration",
                "Projektantrag: Das wirtschaftliches interesse ist belegt.",
                Map.of("budget", "65000"),
                List.of("Release Manager", "Lead Architect")
        );

        List<MatchFinding> findings = matcher.match(doc);
        for (MatchFinding f : findings) {
            System.out.printf("[%s] Rule %s: %s\n", f.status(), f.ruleId(), f.explanation());
        }

        // 3. Compact FastFileFormat Binary Serialization (.matchbin)
        byte[] auditLog = MatcherCodec.encode(findings);
        List<MatchFinding> restored = MatcherCodec.decode(auditLog);
    }
}
```

---

## Key Features

- **⚖️ Automated SOX & Compliance Audits** — Cross-matches regulatory policies (Soll) directly against operational reality (Ist).
- **🧩 3-Layer Hybrid Matching** — Combines symbolic logic (numeric limits, mandatory approvals) with keyword and semantic embedding verification.
- **🔍 Discrepancy & Violation Detection** — Automatically flags missing evidence, unapproved changes, and unauthorized privilege escalation.
- **📦 FastFileFormat `.matchbin` Compression** — Tamper-evident, high-density binary audit trace streaming (Payload ID `0x0007`).
- **🛡️ 100% Air-Gapped & In-Process** — Zero cloud dependencies, zero external database roundtrips, sub-millisecond execution.

---

## Real-World Scenarios

- **🏢 Enterprise SOX Auditing** — Validating that production software deployments exactly match authorized change tickets.
- **📑 BHO & Public Grant Verification** — Checking government grant funding applications against federal budget requirements (BHO.pdf).
- **👥 HR Matrix vs. Active Directory** — Detecting rogue admin privileges and segregation-of-duties (SoD) violations.
- **🔒 Automated Pre-Deployment Gatekeeper** — Blocking CI/CD pipeline deployments if compliance rules are violated.

---

## Performance Benchmarks

FastAIMatcher is profiled using **JMH** to guarantee microsecond-level audit throughput.

| Benchmark Operation | Score (ops/ms) | Throughput | Memory Overhead |
|---|---|---|---|
| **Compliance Rule Matching (50 Rules/Doc)** | **~189,000 ops/ms** | **> 189 Million rules/sec** | **0 bytes allocation** |
| **Binary Audit Report Decoding (`.matchbin`)** | **~223,000 ops/ms** | **> 223 Million findings/sec** | **Zero-Copy Streaming** |
| **Binary Audit Report Encoding (`.matchbin`)** | **~67,000 ops/ms** | **> 67 Million findings/sec** | **Compact VarInt Delta Buffer** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `new FastAIMatcher(rules)` | Initializes rule matcher with list of compliance constraints. |
| `matcher.match(document)` | Executes compliance check against target document and returns findings. |
| `new Rule(id, category, text, keywords, limit)` | Defines a structured compliance condition. |
| `new TargetDocument(id, title, text, kvs, approvers)` | Encapsulates parsed target document artifact. |
| `MatcherCodec.encode(findings)` | Serializes audit findings into compressed FastFileFormat binary byte array. |
| `MatcherCodec.decode(bytes)` | Deserializes `.matchbin` binary bytes back into `List<MatchFinding>`. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Compliance & Violation Demo** | [Demo.java](examples/Demo/src/main/java/fastaimatcher/demo/Demo.java) | `run-demo.bat` | BHO/SOX rule definitions, compliant vs rogue ticket evaluation, and `.matchbin` audit reporting. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastaimatcher/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput 50-rule evaluation benchmarks and binary codec speed. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIMatcher</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileFormat</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastBinary</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastAIMatcher:0.1.0'
    implementation 'com.github.andrestubbe:FastFileFormat:0.1.0'
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ⚖️ **[FastAIMatcher-0.1.0.jar](https://github.com/andrestubbe/FastAIMatcher/releases/download/0.1.0/FastAIMatcher-0.1.0.jar)** (SOX Compliance & Hybrid Matcher)
2. 📄 **[FastFileFormat-0.1.0.jar](https://github.com/andrestubbe/FastFileFormat/releases/download/0.1.0/FastFileFormat-0.1.0.jar)** (Dual Binary & Text File Format)
3. ⚡ **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (VarInt & Binary Packing)
4. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and automated enterprise governance.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastAIReasoner](https://github.com/andrestubbe/FastAIReasoner) — Multi-step reasoning & heuristic rule planning
- [FastAIHybrid](https://github.com/andrestubbe/FastAIHybrid) — Hybrid dense & sparse retrieval fusion
- [FastContentParse](https://github.com/andrestubbe/FastContentParse) — Document section & metadata parsing
- [FastFileFormat](https://github.com/andrestubbe/FastFileFormat) — Universal dual-format binary & text document engine

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
