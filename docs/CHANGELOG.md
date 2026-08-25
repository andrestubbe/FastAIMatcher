# Changelog: FastAIMatcher

All notable changes to this project will be documented in this file.

## [0.1.1] - 2026-08-25
### Changed
- Integrated **FastRegex** zero-allocation streaming pattern dependency for compliance keyword and regex pattern evaluation.

## [0.1.0] - 2026-08-24
### Added
- **Hybrid Compliance Matching Engine (`FastAIMatcher`)**: Symbolic constraint validation and keyword evidence scoring.
- **FastFileFormat Binary Streamer (`MatcherCodec`)**: Compact `.matchbin` audit findings streaming (Payload ID `0x0007`).
- **Target Document Representation (`TargetDocument`, `Rule`)**: Structured SOX/BHO compliance rule model.
- **Interactive Showcase & JMH Benchmark Suite**: Profiling >189M rules/sec evaluated and >223M findings/sec decoded.
