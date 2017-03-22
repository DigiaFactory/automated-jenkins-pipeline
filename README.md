# Script-based automation of Jenkins configurations and pipelines

A pre-configured [Jenkins CI](https://jenkins.io/) setup for easy
continuous integration and deployment alongside GitHub.

The main components are: Jenkins CI, GitHub & [Docker](https://www.docker.com/).
GitHub could later be replaced with GitLab or
Gogs but currently their Jenkins plugins cannot
fully replicate all the functionality in
[github-branch-source-plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin).

This repository is a part of the bachelor's thesis work of
Mikko Piuhola at Metropolia University of Applied Sciences for Digia Plc.
A lot of the groundwork is done in conjuction with the Digia Factory project.

## Usage

### Prerequirements:

- You have a GitHub organization (GitHub or GitHub Enterprise)
- At least one repository contains a `Jenkinsfile`

### Steps

1. Create a file called `.env` in the same directory as `docker-compose.yml`
  - This file should contain all environment variables referenced in `docker-compose.yml`,
  like `CONF_GITHUB_TOKEN`
  - See all available configurations below.
2. Create an access token (see below)
3. Start all services: `docker-compose up -d`
4. Get the initial admin login for Jenkins from the logs:
`docker-compose logs` (will be there after a minute or two)
5. Go to `http://[myaddress]:8080` (e.g. `http://localhost:8080` when running locally)
and login using the initial admin login
6. Jenkins will have auto-configured and scanned the GitHub organization
you specified in the configuration

## Creating an access token

1. **OPTIONAL:** Create a separate user in GitHub for Jenkins to use
2. Create a new personal access token in GitHub
  - `https://github.com/settings/tokens/new?scopes=repo,public_repo,admin:repo_hook,admin:org_hook&description=Jenkins+Access`
3. Provide the GitHub configurations for Docker (see above)
4. You are ready to use it in any Git/GitHub contexts!

## Environment variables for Jenkins configuration

| Name                         | Description                                                           |
|------------------------------|-----------------------------------------------------------------------|
| `CONF_GITHUB_ORG`            | GitHub organization for initial seeded job                            |
| `CONF_GITHUB_TOKEN_USER`     | GitHub token's user (e.g. a jenkins user)                             |
| `CONF_GITHUB_TOKEN`          | GitHub token                                                          |
| `CONF_GITHUB_TOKEN_ID`       | Jenkins ID for the GitHub organization token                          |
| `CONF_SLACK_TEAM_DOMAIN`     | Slack team's domain, leave empty to disable                           |
| `CONF_SLACK_TOKEN`           | Slack's Jenkins integration token                                     |
| `CONF_SLACK_DEFAULT_ROOM`    | Default room to post in Slack                                         |
| `CONF_JENKINS_URL`           | URL where Jenkins is reachable                                        |
| `CONF_JENKINS_NUM_EXECUTORS` | Number of executors, default `4`, optional                            |
| `CONF_SHARED_LIB_NAMES`      | Semicolon-separated list of shared pipeline library names             |
| `CONF_SHARED_LIB_OWNERS`     | Semicolon-separated list of shared pipeline library repository owners |
| `CONF_SHARED_LIB_REPOS`      | Semicolon-separated list of shared pipeline library repository names  |
| `CONF_DOCKER_LOGIN`          | Username for private Docker registry, provided as `docker-login`      |
| `CONF_DOCKER_PASS`           | Password for private Docker registry                                  |
| `EXTRA_HOST_DOCKER_REG`      | Hostname for a private Docker registry that does not have FQDN        |

## Example Jenkinsfile

Create a repository with a `Jenkinsfile` in the root with these contents:

```groovy
pipeline {
    agent any
    
    stages {
        stage('Prepare') {
            steps {
                echo 'Preparing' 
            }
        }
        
        stage('Test') {
            steps {
                parallel 'Smoke tests': {
                    sleep 15
                    echo 'Smoking'
                }, 'Unit tests': {
                    sleep 10
                    echo 'Uniting'
                }
            }
        }

        stage('Skipped stage') {
            when { branch 'non-existent' }
            steps {
                echo 'We will never come here'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying stuff'
            }
        }
    }
}
```

Now you can use that repository for testing your Jenkins installation.

## Shared pipeline libraries

See plugin documentation for info: https://github.com/jenkinsci/workflow-cps-global-lib-plugin/blob/master/README.md#pipeline-shared-libraries

Jenkins Pipelines can use common libraries to abstract complex builds/build steps.
**NOTE:** When using the new declarative pipeline syntax
([pipeline-model-definition-plugin](https://github.com/jenkinsci/pipeline-model-definition-plugin),
you cannot abstract away the whole pipeline, just steps. With "imperative"/current pipeline
syntax, you could have a `Jenkinsfile` as simple as this:

```groovy
@Library('myLib') _

myStandardizedBuild {
  someParameter = 'withAValue'
}
```

### Example

```groovy
@Library('github.com/someowner/somerepo')
import org.someowner.somerepo.*

node {
  stage('VCS') {
    checkout scm
  }

  stage('Build') {
    SomeHelper.doBuild(this)
  }
}
```

## Future plans

- Use official `digia-factory` image for Jenkins when released

