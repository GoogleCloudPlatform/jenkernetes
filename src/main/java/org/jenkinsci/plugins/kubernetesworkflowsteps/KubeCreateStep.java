/*
 * Copyright 2014 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.jenkinsci.plugins.kubernetesworkflowsteps;

import org.apache.http.client.methods.HttpPost;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class KubeCreateStep extends KubeStep {

  public final transient Object payload;
  
  /**
   * @param resource A String that is one of { "pods", "services", "replicationControllers" }
   * @param payload A Groovy Map that defines the resource according to the Kubernetes API
   */
  @DataBoundConstructor
  public KubeCreateStep(String resource, Object payload){
    super(resource);
    this.payload = payload;
  }
  
  @Extension public static final class DescriptorImpl extends AbstractStepDescriptorImpl{
    
    public DescriptorImpl() {
      super(Execution.class);
    }
    
    @Override
    public String getFunctionName(){
      return "kube_create";
    }
    
    @Override
    public String getDisplayName(){
      return "Create a resource using the Kubernetes API";
    }
    
  }
  
  public static class Execution extends KubeStepExecution<KubeCreateStep>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8821023885822732793L;

	/* (non-Javadoc)
     * @see org.jenkinsci.plugins.kubernetesworkflowsteps.KubeStepExecution#request()
     */
    @Override
    protected Object run() throws Exception {
      HttpPost post = new HttpPost(KubeStepExecution.prefix+step.resource);
      post.setEntity(toEntity(step.payload));
      return parse(makeCall(post));
    }
    
  }

}
