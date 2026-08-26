package io.jenkins.plugins.failureprovenance;

import hudson.model.Action;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.model.RunAction2;
import org.springframework.security.access.AccessDeniedException;

public final class FailureProvenanceAction implements RunAction2 {
    private final String signature;
    private final String normalizedExcerpt;
    private final List<Match> matches;
    private transient Run<?, ?> owner;

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
    public void onAttached(Run<?, ?> run) {
        owner = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        owner = run;
    }

    public Run<?, ?> getOwner() {
        return owner;
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

    public static final class Match {
        private final String runId;
        private final double similarity;

        Match(Run<?, ?> build, double similarity) {
            runId = build.getExternalizableId();
            this.similarity = similarity;
        }

        public Run<?, ?> getBuild() {
            try {
                return Run.fromExternalizableId(runId);
            } catch (AccessDeniedException | IllegalArgumentException exception) {
                return null;
            }
        }

        public String getRunId() {
            return runId;
        }

        public boolean isBuildAvailable() {
            return getBuild() != null;
        }

        public String getBuildDisplayName() {
            Run<?, ?> build = getBuild();
            return build == null ? "Deleted build (" + runId + ")" : build.getDisplayName();
        }

        public String getBuildUrl() {
            Run<?, ?> build = getBuild();
            return build == null ? null : build.getUrl();
        }

        public String getSimilarityPercent() {
            return String.format("%.0f%%", similarity * 100);
        }
    }
}
