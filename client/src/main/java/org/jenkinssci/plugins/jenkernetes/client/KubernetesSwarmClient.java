package org.jenkinssci.plugins.jenkernetes.client;
import java.io.IOException;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import hudson.plugins.swarm.Client;
import hudson.plugins.swarm.Options;



public class KubernetesSwarmClient{
  
  private static final Map<String,String> ENV = System.getenv();

  public static void main(String... args) throws InterruptedException,
  IOException {
    Options options = new Options();
    Client client = new Client(options);
    CmdLineParser p = new CmdLineParser(options);
    try {
      p.parseArgument(args);
    } catch (CmdLineException e) {
      System.out.println(e.getMessage());
      p.printUsage(System.out);
      System.exit(-1);
    }
    if (options.help) {
      p.printUsage(System.out);
      System.exit(0);
    }
    // Check to see if passwordEnvVariable is set, if so pull down the
    // password from the env and set as password.
    if (options.passwordEnvVariable != null) {
      options.password = System.getenv(options.passwordEnvVariable);
    }
    
    if(!ENV.containsKey("JENKINS_SERVICE_HOST")){
      System.out.println("No Kubernetes service 'jenkins' found: "
          + "Ensure that you are running this client in a cluster, "
          + "and that your cluster has a service named 'jenkins'");
      System.exit(-1);
    }
    
    
    options.master = "http://" + ENV.get("JENKINS_SERVICE_HOST") + ":" + ENV.get("JENKINS_SERVICE_PORT");
    
    client.run();
  }

}
