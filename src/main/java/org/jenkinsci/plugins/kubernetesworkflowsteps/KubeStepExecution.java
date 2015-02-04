package org.jenkinsci.plugins.kubernetesworkflowsteps;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import hudson.EnvVars;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * TODO: Insert description here. (generated by elibixby)
 */
public abstract class KubeStepExecution<T extends HttpRequestBase, S extends KubeStep> extends AbstractSynchronousStepExecution<String>{
  
  
  @Inject protected transient S step;
  @StepContextParameter private transient EnvVars env;


  
  /* (non-Javadoc)
   * @see org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution#run()
   */
  @Override
  protected String run() throws Exception {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpHost target = new HttpHost(env.expand("KUBERNETES_SERVICE_HOST"), Integer.valueOf(env.expand("KUBERNETES_SERVICE_PORT")));
    CloseableHttpResponse httpresponse = httpclient.execute(target, request());
    try{
      return httpresponse.getEntity().getContent().toString();
    } finally {
      httpresponse.close();
    }
  }
  
  protected abstract T request() throws UnsupportedEncodingException;
  

}