# FastAIMatcher API Reference

## Core Classes

### 1. `fastaimatcher.FastAIMatcher`
* `public FastAIMatcher(List<Rule> rules)`: Initializes compliance matching engine.
* `public List<MatchFinding> match(TargetDocument document)`: Executes compliance verification against document.
* `public void addRule(Rule rule)`: Adds a single rule to active constraint set.
* `public List<Rule> getRules()`: Returns active rule list.

### 2. `fastaimatcher.Rule`
* `record Rule(String id, Category category, String ruleText, List<String> requiredKeywords, double numericThreshold)`: Structured compliance rule definition.

### 3. `fastaimatcher.TargetDocument`
* `record TargetDocument(String docId, String title, String rawText, Map<String, String> keyValues, List<String> approvers)`: Encapsulates parsed target artifact.

### 4. `fastaimatcher.MatchFinding`
* `record MatchFinding(String ruleId, Status status, float confidenceScore, String explanation, String evidenceSnippet)`: Audit finding record.

### 5. `fastaimatcher.MatcherCodec`
* `public static byte[] encode(List<MatchFinding> findings)`: FastFileFormat binary serializer.
* `public static List<MatchFinding> decode(byte[] bytes)`: FastFileFormat binary deserializer.
* `public static void writeToFile(Path path, List<MatchFinding> findings)`: Writes audit report directly to `.matchbin` file.
* `public static List<MatchFinding> readFromFile(Path path)`: Reads audit report directly from `.matchbin` file.
