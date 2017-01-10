#!/usr/bin/env groovy

// Configure basic security
//
// Sets the authorization strategy for Jenkins master
// and creates a default admin user with an initial password

import jenkins.model.*
import hudson.security.*
import org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl
import org.apache.commons.lang.RandomStringUtils

def instance = Jenkins.getInstance()

if (!(instance.getSecurityRealm() instanceof HudsonPrivateSecurityRealm)) {
  instance.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (!(instance.getAuthorizationStrategy() instanceof GlobalMatrixAuthorizationStrategy)) {
  println '--> setting authorization strategy to Global Matrix Authorization Strategy'
  instance.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy())
}

def currentUsers = instance.getSecurityRealm().getAllUsers().collect { it.getId() }

if (!('jenkins-admin' in currentUsers)) {
  println "--> creating local user 'jenkins-admin'"
  println "\n\n\nWARNING: Remember to reset the admin password!"

  // Generate an initial admin password
  int randomStringLength = 32 // Reasonable length for an initial password
  // Initial password, don't get too wild with the characters
  def charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
  def initialAdminPass = RandomStringUtils.random(randomStringLength, charset.toCharArray())

  instance.getSecurityRealm().createAccount('jenkins-admin', initialAdminPass)
  instance.getAuthorizationStrategy().add(Jenkins.ADMINISTER, 'jenkins-admin')

  println "\nInitial admin password is:\n\n${initialAdminPass}\n\n\n"
}

instance.save()
