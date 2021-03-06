package hudson.cli;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.remoting.Callable;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.Argument;

import java.io.IOException;
import java.io.Serializable;

@Extension
public class SetBuildDisplayNameCommand extends CLICommand implements Serializable {

    @Override
    public String getShortDescription() {
        return "Sets the displayName of a build";
    }

    @Argument(metaVar="JOB", usage="Name of the job to build", required=true, index=0)
    public transient AbstractProject<?, ?> job;

    @Argument(metaVar="BUILD#", usage="Number of the build", required=true, index=1)
    public int number;

    @Argument(metaVar="DISPLAYNAME", required=true, usage="DisplayName to be set. '-' to read from stdin.", index=2)
    public String displayName;

    protected int run() throws Exception {
        Run run = job.getBuildByNumber(number);
        run.checkPermission(Run.UPDATE);

        if ("-".equals(displayName)) {
            displayName = channel.call(new Callable<String, IOException>() {
                public String call() throws IOException {
                    return IOUtils.toString(System.in);
                }
            });
        }

        run.setDisplayName(displayName);

        return 0;
    }

}
