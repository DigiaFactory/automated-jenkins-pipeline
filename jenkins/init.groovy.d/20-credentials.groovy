#!/usr/bin/env groovy

// Create a set if initial credentials for Jenkins master.
//
// All credentials should be read from environment variables or files
// provided to Jenkins.
// Environment variables can be provided via Kontena Vault or similar
// where they are not stored in version control but provided on
// container create.

import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret

def env = System.getenv()
def store = SystemCredentialsProvider.getInstance().getStore()

// Token for initial GitHub organization
def confGitHubTokenUser = env['CONF_GITHUB_TOKEN_USER']
def confGitHubToken = env['CONF_GITHUB_TOKEN']
def confGitHubTokenId = env['CONF_GITHUB_TOKEN_ID']

// Login for private Docker registry
def dockerLogin = env['CONF_DOCKER_LOGIN'] // TODO: Allow multiple
def dockerPass = env['CONF_DOCKER_PASS']

if (confGitHubTokenUser && confGitHubToken) {
  println "--> creating username & password credentials for ${confGitHubTokenUser}"
  def githubOrgCred =  new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    confGitHubTokenId,
    'GitHub organization token',
    confGitHubTokenUser,
    confGitHubToken
  )

  // Some plugins use the token via secret text, so provide that too
  println "--> creating secret text credentials for ${confGitHubTokenUser}"
  def githubOrgSecretText = new StringCredentialsImpl(
    CredentialsScope.GLOBAL,
    "${confGitHubTokenId}-text",
    'GitHub organization token as secret text',
    Secret.fromString(confGitHubToken)
  )

  // Add all credentials to Jenkins
  store.addCredentials(Domain.global(), githubOrgCred)
  store.addCredentials(Domain.global(), githubOrgSecretText)

} else {
  println 'DEBUG: No CONF_GITHUB_TOKEN_USER or CONF_GITHUB_TOKEN defined, skipping GitHub credentials'
}

if (dockerLogin && dockerPass) {
  println "--> creating 'docker-login' credentials for ${dockerLogin}"
  def dockerLoginCred = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    'docker-login', // TODO: Allow multiple
    'Docker registry login',
    dockerLogin,
    dockerPass
  )

  store.addCredentials(Domain.global(), dockerLoginCred)
} else {
  println 'DEBUG: No CONF_DOCKER_LOGIN or CONF_DOCKER_PASS defined, skipping Docker credentials'
}

