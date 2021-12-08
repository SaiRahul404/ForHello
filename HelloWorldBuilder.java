package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jenkinsci.Symbol;

import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String name;
    private boolean useFrench;
    private final String Company;
    private final String location;

    @DataBoundConstructor
    public HelloWorldBuilder(String name,String Company,String location) {
        this.name = name;
        this.Company=Company;
        this.location=location;
    }

    public String getName() {
        return name;
    }
   
    public boolean isUseFrench() {
        return useFrench;
    }
    
    public String getCompany() {
    	return Company;
    }
    public String getlocation() {
    	return location;
    }

    @DataBoundSetter
    public void setUseFrench(boolean useFrench) {
        this.useFrench = useFrench;
    }
    

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        if (useFrench) {
            listener.getLogger().println("Bonjour, " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!");
        }
        listener.getLogger().println("How is , " + Company + "!");
        
      //  Unirest.setTimeouts(0, 0);
     
        try {
        	HttpResponse<String> response = Unirest.get("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+location+"/today?key=JAFPFLE3VEZVAMFSDFELNCJXJ&include=obs%2Cfcst%2Cstats%2Calerts%2Ccurrent%2Chistfcst&elements=tempmax,tempmin,temp").asString();
        	JSONObject json = (JSONObject) JSONSerializer.toJSON(response.getBody()); 
        	String temp =json.getString("currentConditions");
        	listener.getLogger().println("The temperature at , " + location + temp);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
        
        
      
        
        
        
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

    }

}
