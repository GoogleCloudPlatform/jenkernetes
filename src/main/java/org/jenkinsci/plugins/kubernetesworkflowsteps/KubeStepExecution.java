package org.jenkinsci.plugins.kubernetesworkflowsteps;

import com.github.kubernetes.java.client.v2.KubernetesApiClient;

import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import hudson.EnvVars;

import javax.inject.Inject;

public abstract class KubeStepExecution<T, O, K extends KubeStep<T,O>> extends AbstractSynchronousStepExecution<O>{
  
  @SuppressWarnings("unused")
  @Inject protected transient K step;
  @StepContextParameter private transient EnvVars env;
  
  protected KubernetesApiClient getKubeClient(){
    String kube_host_ip = env.get("KUBERNETES_MASTER_HOST", "0.0.0.0");
    String kube_host_port = env.get("KUBERNETES_MASTER_PORT","0000");
    return new KubernetesApiClient("tcp://"+kube_host_ip+":"+kube_host_port, "admin","");
  }
}