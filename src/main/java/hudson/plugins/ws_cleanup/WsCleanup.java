package hudson.plugins.ws_cleanup;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import net.sf.json.JSONObject;

/**
 *
 * @author dvrzalik
 */
public class WsCleanup extends Notifier {

    private final List<Pattern> patterns;
    private final boolean deleteDirs;

    @DataBoundConstructor
    // FIXME can't get repeteable to work with a List<String>
    public WsCleanup(List<Pattern> patterns, boolean deleteDirs) {
        this.patterns = patterns;
        this.deleteDirs = deleteDirs;
    }

    public List<Pattern> getPatterns(){
		return patterns;
	}
    
    public boolean getDeleteDirs(){
    	return deleteDirs;
    }
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().append("\nDeleting project workspace... ");
        FilePath workspace = build.getWorkspace();
        try {
        	if (workspace == null || !workspace.exists()) 
                return true;
            if (patterns == null || patterns.isEmpty()) {
                workspace.deleteRecursive();
            } else {
                workspace.act(new Cleanup(patterns,deleteDirs));
            }
            listener.getLogger().append("done\n\n");
        } catch (InterruptedException ex) {
            Logger.getLogger(WsCleanup.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService(){
    	return BuildStepMonitor.STEP;
    }
    
    @Extension(ordinal=-99999)
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            super(WsCleanup.class);
        }

        @Override
        public String getDisplayName() {
            return Messages.WsCleanup_Delete_workspace();
        }

        @Override
        public boolean isApplicable(Class clazz) {
            return true;
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }
    }


}
