#!/usr/bin/env groovy

import jenkins.model.*
import hudson.security.*
import org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl

println "--> creating local user 'jenkins-admin' with password 'admin'"
println "\n\n\nWARNING: Remember to reset the admin password!\n\n\n"

def instance = Jenkins.getInstance()

if (!(instance.getSecurityRealm() instanceof HudsonPrivateSecurityRealm)) {
  instance.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (!(instance.getAuthorizationStrategy() instanceof GlobalMatrixAuthorizationStrategy)) {
  instance.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy())
}

def currentUsers = instance.getSecurityRealm().getAllUsers().collect { it.getId() }

if (!('jenkins-admin' in currentUsers)) {
  instance.getSecurityRealm().createAccount('jenkins-admin', 'admin')
  instance.getAuthorizationStrategy().add(Jenkins.ADMINISTER, 'jenkins-admin')
}

instance.save()
