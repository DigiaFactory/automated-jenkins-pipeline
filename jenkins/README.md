# Pre-configured Jenkins Docker image (WIP)

Heavily based on the [official Jenkins Docker image](https://github.com/jenkinsci/docker/)

The general changes to the base Docker images are based on
[DigiaCloudServices/digia-factory build-pipeline-in-a-minute/jenkins](https://github.com/DigiaCloudServices/digia-factory)
and this image will extend that base image when it is finished.

## Contents

- `Dockerfile`: Docker image
- `plugins.txt`: Configuration of what plugins to install
- `init.groovy.d`: Jenkins auto-configuration (see below)
- `scripts`: Internal start-up and configuration scripts
- `seed-jobs`: Job DSL configurations for seeded jobs (see below)

## Usage

See the main README

## Initial configuration

Jenkins can be relatively easily configured via Groovy scripts.
Add any configuration scripts to `init.groovy.d`
and they will be executed on initial boot.

Some configuration is provided for the purposes of this automated stack,
like a way to provide GitHub credentials, a Slack configuration and
a mechanism to seed Jenkins jobs via [Job DSL](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin).

### Example

```groovy
#!/usr/bin/env groovy
import hudson.model.*;
import jenkins.model.*;

def env = System.getenv()

// Set number of executors on master
def execCount = env['CONF_JENKINS_NUM_EXECUTORS']?.toInteger() ?: 4
Jenkins.instance.setNumExecutors(execCount)
```

## Seeding jobs

This Docker image is configured to seed initial jobs
defined in the `seed-jobs/` directory. Place any jobs
that should be automatically configured (e.g. a GitHub Organization Folder)
as Groovy/Job DSL scripts inside that directory,
and they will be created on initial boot.

### Example

From: https://github.com/jenkinsci/job-dsl-plugin/wiki/Tutorial---Using-the-Jenkins-Job-DSL

```groovy
job('DSL-Tutorial-1-Test') {
    scm {
        git('git://github.com/quidryan/aws-sdk-test.git')
    }
    triggers {
        scm('H/15 * * * *')
    }
    steps {
        maven('-e clean test')
    }
}
```

