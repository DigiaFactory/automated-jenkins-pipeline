#!/usr/bin/env groovy

// Seed initial Jenkins jobs
//
// Create jobs using Jobs DSL using
// the files in ../seed-jobs/

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import groovy.io.FileType

def scriptFiles = []
def seedJobsDir = '/usr/share/jenkins/ref/seed-jobs'

def dir = new File(seedJobsDir)
dir.eachFileRecurse (FileType.FILES) { file ->
  scriptFiles << file
}

println "--> seeding jobs from ${seedJobsDir}"
scriptFiles.each { script ->
  def workspace = new File('.')
  def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)

  new DslScriptLoader(jobManagement).runScript(script.text)
}

