Using the org.reficio is the only solution I know of to satisfy the
Require-Capabilties headers that the new consider-maven functionality
leaves us with, as it doesn't require P2 resolution in its processing.

Since Jetty already builds its jars as bundles, the current setup
mainly renames the jars and should leave the bundle headers unchanged,
at least until the signing happens.