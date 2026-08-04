package io.jenkins.plugins.failureprovenance;

import hudson.model.Action;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FailureProvenanceAction implements Action {
    private final String signature;
    private final String normalizedExcerpt;
    private final List<Match> matches;

    FailureProvenanceAction(String signature, String normalizedExcerpt, List<Match> matches) {
        this.signature = signature;
        this.normalizedExcerpt = normalizedExcerpt;
        this.matches = Collections.unmodifiableList(new ArrayList<>(matches));
    }

    public String getSignature() {
        return signature;
    }

    public String getShortSignature() {
        return signature.substring(0, 12);
    }

    public String getNormalizedExcerpt() {
        return normalizedExcerpt;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public boolean isNewFailure() {
        return matches.isEmpty();
    }

    @Override
    public String getIconFileName() {
        return "symbol-search plugin-ionicons-api";
    }

    @Override
    public String getDisplayName() {
        return "Failure Provenance";
    }

    @Override
    public String getUrlName() {
        return "failure-provenance";
    }

    static final class Match {
        private final Run<?, ?> build;
        private final double similarity;

        Match(Run<?, ?> build, double similarity) {
            this.build = build;
            this.similarity = similarity;
        }

        public Run<?, ?> getBuild() {
            return build;
        }

        public String getSimilarityPercent() {
            return String.format("%.0f%%", similarity * 100);
        }
    }
}
