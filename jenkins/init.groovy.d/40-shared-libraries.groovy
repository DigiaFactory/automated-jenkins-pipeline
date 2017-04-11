#!/usr/bin/env groovy

// Configure globally shared Pipeline libraries
//
// Libraries are defined by these environment variables:
//  - CONF_SHARED_LIB_NAMES: names to call libraries
//  - CONF_SHARED_LIB_OWNERS: repository owners
//  - CONF_SHARED_LIB_REPOS: library repositories
// There should be an equal amount of entries in each configuration

import java.util.ArrayList;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource
import org.jenkinsci.plugins.workflow.libs.*

def env = System.getenv()
def separator = ';'

// These configurations are lists separated by a character
def sharedLibs = env['CONF_SHARED_LIB_NAMES']?.split(separator) ?: new String[0]
def sharedLibOwners = env['CONF_SHARED_LIB_OWNERS']?.split(separator) ?: new String[0]
def sharedLibRepos = env['CONF_SHARED_LIB_REPOS']?.split(separator) ?: new String[0]

if (sharedLibs.size() == 0) {
  println 'DEBUG: no shared pipeline libraries configured in CONF_SHARED_LIB_NAMES, skipping'
  return
}

def libs = new ArrayList<LibraryConfiguration>()

// Currently, GlobalLibraries requires all libraries
// to be set in one go, so gather them into an ArrayList.
sharedLibs.eachWithIndex { libName, ind ->
  def gitHubId = null
  def gitHubApiUri = null
  def gitHubCredentialsId = env['CONF_GITHUB_TOKEN_ID'] // TODO: Allow multiple?
  def sharedLibOwner = sharedLibOwners.getAt(ind)
  def sharedLibRepoName = sharedLibRepos.getAt(ind)

  def libConf = new LibraryConfiguration(libName,
    new SCMSourceRetriever(new GitHubSCMSource(
      gitHubId,
      gitHubApiUri,
      GitHubSCMSource.DescriptorImpl.SAME,
      gitHubCredentialsId,
      sharedLibOwner,
      sharedLibRepoName
    ))
  )
  libConf.defaultVersion = 'master' // Could be configurable, override enough for now
  libConf.implicit = false // There could be a lot of libraries, don't autoload anything
  libConf.allowVersionOverride = true // Allows users to specify a version, e.g. @Library('myLib@2.0.0')

  libs.add(libConf)
}

GlobalLibraries.get().setLibraries(libs)

