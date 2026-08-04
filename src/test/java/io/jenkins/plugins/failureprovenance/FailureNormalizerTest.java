package io.jenkins.plugins.failureprovenance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FailureNormalizerTest {
    @Test
    void removesVolatileValues() {
        String normalized = FailureNormalizer.normalize(
                "2026-08-04T12:15:30Z ERROR /var/lib/jenkins/workspace/widget/build 123e4567-e89b-12d3-a456-426614174000 at :8080");

        assertEquals("<timestamp> error <workspace> <uuid> at :<port>", normalized);
    }

    @Test
    void treatsSimilarMessagesAsSimilar() {
        String first = FailureNormalizer.normalize("ERROR request 5b0c0f8e0ebca1ba1e31 timed out at 2026-08-04T12:15:30Z");
        String second = FailureNormalizer.normalize("ERROR request 8c967a8e66cd50a34e51 timed out at 2026-08-04T12:16:30Z");

        assertTrue(FailureSignature.similarity(first, second) > 0.9);
    }
}
