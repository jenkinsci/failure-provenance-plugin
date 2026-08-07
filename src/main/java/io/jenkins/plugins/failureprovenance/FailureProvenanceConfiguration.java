package io.jenkins.plugins.failureprovenance;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public final class FailureProvenanceConfiguration extends GlobalConfiguration {
    static final int DEFAULT_HISTORY_LIMIT = 50;
    static final int MAX_HISTORY_LIMIT = 500;

    private int historyLimit = DEFAULT_HISTORY_LIMIT;

    public FailureProvenanceConfiguration() {
        load();
    }

    public int getHistoryLimit() {
        return historyLimit;
    }

    @DataBoundSetter
    public void setHistoryLimit(int historyLimit) {
        this.historyLimit = Math.max(1, Math.min(historyLimit, MAX_HISTORY_LIMIT));
        save();
    }

    public static FailureProvenanceConfiguration get() {
        return GlobalConfiguration.all().get(FailureProvenanceConfiguration.class);
    }
}
