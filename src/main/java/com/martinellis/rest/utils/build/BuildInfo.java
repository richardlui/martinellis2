package com.martinellis.rest.utils.build;

public class BuildInfo {
	private static final String UNAVAILABLE = "unavailable";

    private static class GitInfo {
        private String buildNumber;
        private String revision;
        private String branch;
        private String tag;

        public GitInfo(String buildNumber, String revision, String branch, String tag) {
            this.buildNumber = buildNumber;
            this.revision = revision;
            this.branch = branch;
            this.tag = tag;
        }

    }

    private static class JenkinsInfo {
        private String buildnumber;
        private String buildId;
        private String buildUrl;
        private String buildTag;

        public JenkinsInfo(String buildnumber, String buildId, String buildUrl, String buildTag) {
            this.buildnumber = buildnumber;
            this.buildId = buildId;
            this.buildUrl = buildUrl;
            this.buildTag = buildTag;
        }
    }

    private final GitInfo gitInfo;
    private final JenkinsInfo jenkinsInfo;

    public BuildInfo(String buildnumber, String buildId, String buildUrl, String buildTag, String gitBuildNumber,
            String gitRevision, String gitBranch, String gitTag) {
        gitInfo = new GitInfo(gitBuildNumber, gitRevision, gitBranch, gitTag);
        jenkinsInfo = new JenkinsInfo(buildnumber, buildId, buildUrl, buildTag);
    }

    public BuildInfo() {
        gitInfo = new GitInfo(UNAVAILABLE, UNAVAILABLE, UNAVAILABLE, UNAVAILABLE);
        jenkinsInfo = new JenkinsInfo(UNAVAILABLE, UNAVAILABLE, UNAVAILABLE, UNAVAILABLE);
    }

    public String getBuildnumber() {
        return valueOrDefault(jenkinsInfo.buildnumber);
    }

    public String getBuildId() {
        return valueOrDefault(jenkinsInfo.buildId);
    }

    public String getBuildUrl() {
        return valueOrDefault(jenkinsInfo.buildUrl);
    }

    public String getBuildTag() {
        return valueOrDefault(jenkinsInfo.buildTag);
    }

    public String getGitBuildNumber() {
        return valueOrDefault(gitInfo.buildNumber);
    }

    public String getGitRevision() {
        return valueOrDefault(gitInfo.revision);
    }

    public String getGitBranch() {
        return valueOrDefault(gitInfo.branch);
    }

    public String getGitTag() {
        return valueOrDefault(gitInfo.tag);
    }

    private String valueOrDefault(String value) {
        return value != null ? value : UNAVAILABLE;
    }

    public String toLogString() {
        return String.format("build %s (%s)", gitInfo.buildNumber, jenkinsInfo.buildnumber);
    }
}
