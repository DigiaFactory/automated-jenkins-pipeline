#!/usr/bin/env groovy

// Seed a single GitHub organization multibranch folder
// that auto-scans all repositories and their branches.
// TODO: Configure multiple organizations similarly to
// to Shared Pipeline Libraries.

import javaposse.jobdsl.dsl.*

// Include access to Jenkins.instance for inspecting e.g `Jenkins.instance.allItems.each{p-> println "job:" +p.name}` or `Jenkins.instance.getItemByFullName('somejobname')`
import jenkins.model.*
import hudson.model.*

def env = System.getenv()
def jobName = env['CONF_GITHUB_ORG'] // Use organization's name as the project name

// Need to use Jenkins Credentials global secret username-password for Github username and Github Personal Access Token as follows:
// https://github.com/settings/tokens/new?scopes=repo,public_repo,admin:repo_hook,admin:org_hook&description=Jenkins+Access
def jenkinsGitHubUserAccesstokenId = 'github-org-token'

def seedJob = job(jobName) {
    displayName jobName
    configure { project ->
        // This job config.xml is for a Jenkins 2.0 type Github Organization Folder
        project.name = 'jenkins.branch.OrganizationFolder'
        // Jenkins will automatically pin plugin version attributes for the config.xml, but can also set attributes for top node like this: project.attributes()['attrib'] = 'something'
        project / 'projectFactories' / 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProjectFactory' {}
        project / 'icon' (class: 'jenkins.branch.MetadataActionFolderIcon') / 'owner' (class: 'jenkins.branch.OrganizationFolder', reference: '../..') // sets class and reference attributes
        project / 'properties' / 'jenkins.branch.NoTriggerOrganizationFolderProperty' {
            branches '.*'
        }
        project / 'folderViews' (class: 'jenkins.branch.OrganizationFolderViewHolder') / 'owner' (reference: '../..')
        project / 'healthMetrics' / 'com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric'
        project / 'orphanedItemStrategy' (class: 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy') {
            pruneDeadBranches true
            daysToKeep 0
            numToKeep 3
        }
        // Trigger repository scans every 1 hour
        project / 'triggers' / 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
            spec 'H * * * *'
            interval 3600000
        }
        project / 'navigators' / 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' {
            repoOwner "$jobName"
            scanCredentialsId "$jenkinsGitHubUserAccesstokenId"
            checkoutCredentialsId 'SAME'
            pattern '.*'
            buildOriginBranch true
            buildOriginBranchWithPR true
            buildOriginPRMerge false
            buildOriginPRHead false
            buildForkPRMerge false
            buildForkPRHead false
        }

        // For Jenkins 2.0 Organization Folder job - some of the FreestyleJob generated config.xml is not needed.
        project.remove(project / scm)
        project.remove(project / publishers)
        project.remove(project / builders)
        project.remove(project / buildWrappers)
        project.remove(project / concurrentBuild)
        project.remove(project / blockBuildWhenDownstreamBuilding)
        project.remove(project / blockBuildWhenUpstreamBuilding)
        project.remove(project / keepDependencies)
        project.remove(project / canRoam)
        project.remove(project / disabled)
    }
}

// Want to immediately schedule a build of the new job for computing sub-folders
// so that branches and pull-requests of any origin get included.
queue(seedJob) // https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands
