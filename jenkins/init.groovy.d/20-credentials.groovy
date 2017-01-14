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

if (!confGitHubTokenUser || !confGitHubToken) {
  println 'INFO: No CONF_GITHUB_TOKEN_USER or CONF_GITHUB_TOKEN defined, skipping credentials'
  return
}

println "--> creating username & password credentials for ${confGitHubTokenUser}"
def githubOrgCred =  new UsernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  'github-org-token',
  'GitHub organization token',
  confGitHubTokenUser,
  confGitHubToken
)

// Some plugins use the token via secret text, so provide that too
println "--> creating secret text credentials for ${confGitHubTokenUser}"
def githubOrgSecretText = new StringCredentialsImpl(
  CredentialsScope.GLOBAL,
  'github-org-token-text',
  'GitHub organization token as secret text',
  Secret.fromString(confGitHubToken)
)

// Add all credentials to Jenkins
store.addCredentials(Domain.global(), githubOrgCred)
store.addCredentials(Domain.global(), githubOrgSecretText)

