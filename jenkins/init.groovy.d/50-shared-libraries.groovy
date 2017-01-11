#!/usr/bin/env groovy

// Configure globally shared Pipeline libraries

import java.util.ArrayList;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource
import org.jenkinsci.plugins.workflow.libs.*

def env = System.getenv()
def separator = ';'

def sharedLibs = env['CONF_SHARED_LIB_NAMES']?.split(separator) ?: ''
def sharedLibOwners = env['CONF_SHARED_LIB_OWNERS']?.split(separator) ?: ''
def sharedLibRepos = env['CONF_SHARED_LIB_REPOS']?.split(separator) ?: ''

if (!env['CONF_SHARED_LIB_NAMES'].size() == 0) {
  println '--> no shared pipeline libraries configured in CONF_SHARED_LIB_NAMES, skipping'
  return
}

def libs = new ArrayList<LibraryConfiguration>()

sharedLibs.eachWithIndex { libName, ind ->
  def gitHubId = null
  def gitHubApiUri = null
  def gitHubCredentialsId = 'github-org-token' // TODO: Be more dynamic here, remove hard-coding
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
  libConf.implicit = false
  libConf.allowVersionOverride = true

  libs.add(libConf)
}

GlobalLibraries.get().setLibraries(libs)

