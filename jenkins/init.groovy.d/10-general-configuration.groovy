#!/usr/bin/env groovy

// Init script for Jenkins
// You can add your own by copying them into
// /usr/share/jenkins/ref/init.groovy.d/

import jenkins.model.*;
import jenkins.CLI;

def instance = Jenkins.getInstance()
def env = System.getenv()
def jlc = JenkinsLocationConfiguration.get()

def url = env['CONF_JENKINS_URL']
def execCount = env['CONF_JENKINS_NUM_EXECUTORS']?.toInteger() ?: 4
def slaveAgentPort = env['JENKINS_SLAVE_AGENT_PORT']?.toInteger() ?: 49187

Thread.start {

  // Configure Jenkin's external URL
  if (jlc.getUrl() != url) {
    println "--> setting Jenkins url to ${url}"
    jlc.setUrl(url)
    jlc.save()
  }

  // Jenkins 2.54+ disables CLI remoting due to security and performance issues
  // See: https://jenkins.io/blog/2017/04/11/new-cli/
  // but at least currently it is only done in the SetupWizard which we are skipping.
  println '--> disabling CLI remoting for security'
  CLI.get().setEnabled(false);

  println "--> setting number of executors on master to ${execCount}"
  instance.setNumExecutors(execCount)

  sleep 10000 // Wait for Jenkins instance to boot
  println "--> setting agent port for jnlp to ${slaveAgentPort}"
  instance.setSlaveAgentPort(slaveAgentPort)
  instance.save()
  println "--> setting agent port for jnlp... done"

}
