package io.jenkins.plugins.failureprovenance;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Extension
public final class FailureProvenanceListener extends RunListener<Run<?, ?>> {
    private static final int LOG_LINE_LIMIT = 300;
    private static final int HISTORY_LIMIT = 50;
    private static final double SIMILARITY_THRESHOLD = 0.72;

    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        if (!Result.FAILURE.equals(run.getResult()) && !Result.UNSTABLE.equals(run.getResult())) {
            return;
        }
        try {
            String normalizedExcerpt = FailureNormalizer.normalize(selectRelevantLines(run.getLog(LOG_LINE_LIMIT)));
            if (normalizedExcerpt.isBlank()) {
                return;
            }
            String signature = FailureSignature.sha256(normalizedExcerpt);
            List<FailureProvenanceAction.Match> matches = findMatches(run, normalizedExcerpt, signature);
            run.addAction(new FailureProvenanceAction(signature, normalizedExcerpt, matches));
            listener.getLogger().println("[failure-provenance] signature=" + signature.substring(0, 12)
                    + " matches=" + matches.size());
        } catch (IOException exception) {
            listener.getLogger().println("[failure-provenance] Could not inspect console output: " + exception.getMessage());
        }
    }

    private static String selectRelevantLines(List<String> lines) {
        List<String> selected = new ArrayList<>();
        for (String line : lines) {
            String lower = line.toLowerCase();
            if (lower.contains("error") || lower.contains("exception") || lower.contains("fail")
                    || lower.contains("timeout") || lower.contains("fatal")) {
                selected.add(line);
            }
        }
        return String.join("\n", selected.isEmpty() ? lines : selected);
    }

    private static List<FailureProvenanceAction.Match> findMatches(
            Run<?, ?> run, String normalizedExcerpt, String signature) {
        List<FailureProvenanceAction.Match> matches = new ArrayList<>();
        int inspected = 0;
        Run<?, ?> candidate = run.getPreviousBuild();
        while (candidate != null && inspected < HISTORY_LIMIT) {
            inspected++;
            FailureProvenanceAction action = candidate.getAction(FailureProvenanceAction.class);
            if (action != null) {
                double similarity = signature.equals(action.getSignature())
                        ? 1.0
                        : FailureSignature.similarity(normalizedExcerpt, action.getNormalizedExcerpt());
                if (similarity >= SIMILARITY_THRESHOLD) {
                    matches.add(new FailureProvenanceAction.Match(candidate, similarity));
                }
            }
            candidate = candidate.getPreviousBuild();
        }
        return matches;
    }
}
