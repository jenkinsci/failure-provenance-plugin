# Failure Provenance Plugin

Failure Provenance helps a Jenkins user answer the first question after a red build: **has this failure happened before?**

For every failed or unstable build, the plugin creates a normalized signature from the relevant console output and compares it with earlier builds of the same job. The build page shows whether the signature is new, how often it has occurred, and links to matching builds.

## What it does

- Extracts a bounded error-focused console excerpt after a failed or unstable build.
- Normalizes volatile values such as timestamps, UUIDs, workspace paths, ports, build numbers, and hex identifiers.
- Stores a SHA-256 signature and a short redacted excerpt with the build.
- Finds exact and near matches among the previous 50 builds of the same job.
- Keeps the conclusion evidence-based: it reports recurrence, never claims root cause or automatically retries a build.

## Installation

Build the plugin with Java 17 or newer:

```bash
mvn verify
```

Upload `target/failure-provenance.hpi` through **Manage Jenkins -> Plugins -> Advanced settings**.

## Current limits

This first release analyzes console output only and compares history within the same Jenkins job. JUnit-aware extraction, multibranch matching, and optional local semantic embeddings are planned follow-ups.

## License

MIT.
