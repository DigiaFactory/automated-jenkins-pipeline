#!/usr/bin/env groovy

// Init script for Jenkins
// You can add your own by copying them into
// /usr/share/jenkins/ref/init.groovy.d/

import hudson.model.*;
import jenkins.model.*;

def instance = Jenkins.getInstance()
def env = System.getenv()
def jlc = JenkinsLocationConfiguration.get()

// Configure Jenkin's external URL
if (jlc.getUrl() != env['CONF_JENKINS_URL']) {
  println "--> setting Jenkins url to ${env['CONF_JENKINS_URL']}"
  jlc.setUrl(env['CONF_JENKINS_URL'])
  jlc.save()
}

// Set number of executors on master
def execCount = env['CONF_JENKINS_NUM_EXECUTORS']?.toInteger() ?: 4
println "--> setting number of executors on master to ${execCount}"
instance.setNumExecutors(execCount)

Thread.start {
  sleep 10000 // Wait for Jenkins instance to boot
  println "--> setting agent port for jnlp"
  int port = env['JENKINS_SLAVE_AGENT_PORT'].toInteger()
  instance.setSlaveAgentPort(port)
  instance.save()
  println "--> setting agent port for jnlp... done"
}
