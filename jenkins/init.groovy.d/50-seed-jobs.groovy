#!/usr/bin/env groovy

// Seed initial Jenkins jobs
//
// Create jobs using Jobs DSL using
// the files in ../seed-jobs/

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import groovy.io.FileType

def scriptFiles = []
def seedJobsDir = System.getenv('SEED_JOBS_DIR')

// Find all Job DSL files in directory
def dir = new File(seedJobsDir)
dir.eachFileRecurse (FileType.FILES) { file ->
  if (file.name.endsWith('.groovy')) {
    scriptFiles << file
  }
}

println "--> seeding jobs from ${seedJobsDir}"
scriptFiles.each { script ->
  def workspace = new File('.')
  def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)

  new DslScriptLoader(jobManagement).runScript(script.text)
}

