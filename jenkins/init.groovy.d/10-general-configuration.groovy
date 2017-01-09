#!/usr/bin/env groovy

// Init script for Jenkins
// You can add your own by copying them into
// /usr/share/jenkins/ref/init.groovy.d/

import hudson.model.*;
import jenkins.model.*;

def env = System.getenv()
def jlc = JenkinsLocationConfiguration.get()

// Configure Jenkin's external URL
println "--> setting Jenkins url to ${env['CONF_JENKINS_URL']}"
jlc.setUrl(env['CONF_JENKINS_URL'])
jlc.save()

// Set number of executors on master
def execCount = env['CONF_JENKINS_NUM_EXECUTORS']?.toInteger() ?: 4
println "--> setting number of executors on master to ${execCount}"
Jenkins.instance.setNumExecutors(execCount)

Thread.start {
      sleep 10000
      println "--> setting agent port for jnlp"
      int port = env['JENKINS_SLAVE_AGENT_PORT'].toInteger()
      Jenkins.instance.setSlaveAgentPort(port)
      println "--> setting agent port for jnlp... done"
}
