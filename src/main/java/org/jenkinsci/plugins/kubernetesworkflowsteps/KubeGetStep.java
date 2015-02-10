package org.jenkinsci.plugins.kubernetesworkflowsteps;

import org.apache.http.client.methods.HttpGet;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.kohsuke.stapler.DataBoundConstructor;


import hudson.Extension;

/**
 * TODO: Insert description here. (generated by elibixby)
 */
public class KubeGetStep extends KubeStep {


  public final transient String id;
  
  /**
   * @param resource
   */
  @DataBoundConstructor
  public KubeGetStep(String resource, String id) {
    super(resource);
    this.id = id;
  }

  @Extension public static final class DescriptorImpl extends AbstractStepDescriptorImpl{

    public DescriptorImpl() {
      super(Execution.class);
    }
    
    @Override
    public String getFunctionName(){
      return "kube_get";
    }
    
    @Override
    public String getDisplayName(){
      return "Get a resource using the Kubernetes API";
    }
    
  }
  
  
  public static class Execution extends KubeStepExecution<KubeGetStep>{

    @Override
    protected Object run() throws Exception {
      return parse(makeCall(new HttpGet(this.prefix+step.resource+"/"+step.id),
          this.readOnlyHost, this.readOnlyPort));
    }
  }
}
