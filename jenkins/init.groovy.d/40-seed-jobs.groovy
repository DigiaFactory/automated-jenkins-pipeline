#!/usr/bin/env groovy

// Seed initial Jenkins jobs
//
// Create jobs using Jobs DSL using
// the files in ../seed-jobs/

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

// TODO: Loop over directory
def jobDslScript = new File('/usr/share/jenkins/ref/seed-jobs/github-org.groovy')
def workspace = new File('.')

def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)

new DslScriptLoader(jobManagement).runScript(jobDslScript.text)
