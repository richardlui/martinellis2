package com.martinellis.rest.utils.build;

import com.martinellis.rest.utils.build.BuildInfo;
import com.martinellis.rest.utils.config.Environment;

public class BuildInfoFactory {
	private Environment env;

    public BuildInfoFactory(Environment env) {
        this.env = env;
    }

    public BuildInfo getBuildInfo() {
        String gitBuildNumber = env.getString("build.git.buildnumber");
        String gitRevision = env.getString("build.git.revision");
        String gitBranch = env.getString("build.git.branch");
        String gitTag = env.getString("build.git.tag");

        String jenkinsBuildNumber = env.getString("build.jenkins.buildnumber");
        String jenkinsBuildId = env.getString("build.jenkins.id");
        String jenkinsBuildUrl = env.getString("build.jenkins.url");
        String jenkinsBuildTag = env.getString("build.jenkins.tag");

        return new BuildInfo(jenkinsBuildNumber, jenkinsBuildId, jenkinsBuildUrl, jenkinsBuildTag, gitBuildNumber,
                gitRevision, gitBranch, gitTag);
    }
}
