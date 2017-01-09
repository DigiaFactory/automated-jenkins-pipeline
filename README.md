# Automated Jenkins CI stack

**NOTE: Work in progress!**

This repository hosts a pre-configured Jenkins CI stack for easy
continuous integration and deployment alongside GitHub.

The main components are: Jenkins CI & GitHub. GitHub could later
be replaced with GitLab or Gogs but currently their Jenkins plugins
cannot fully replicate all the functionality in
[github-organization-folder-plugin](https://github.com/jenkinsci/github-organization-folder-plugin).

This repository is a part of the bachelor's thesis work of
Mikko Piuhola at Metropolia University of Applied Sciences for Digia Plc.
A lot of the groundwork is done in conjuction with the Digia Factory project.

## Usage

### Prerequirements:

- You have a GitHub organization (GitHub or GitHub Enterprise)
- At least one repository contains a `Jenkinsfile`

Create a file called `.env` in the same directory as `docker-compose.yml`.
This file should contain all environment variables
referenced in `docker-compose.yml`, like `CONF_GITHUB_TOKEN`.

See all available configurations below.

### Steps

0. Create an access token (see below)
1. Start all services: `docker-compose up -d`
2. Get the initial admin login for Jenkins from the logs:
`docker-compose logs` (will be there after a minute or two)
3. Go to `[myaddress]:8080` (e.g. `http://localhost:8080` when running locally)
and login using the initial admin login
4. A folder with jobs should be configured with the GitHub
organization you specified in the configuration

## Creating an access token

**OPTIONAL:** Create a separate user in GitHub for Jenkins to use

1. Create a new personal access token in GitHub
  - `https://github.com/settings/tokens/new?scopes=repo,public_repo,admin:repo_hook,admin:org_hook&description=Jenkins+Access`
2. Provide the GitHub configurations for Docker (see above)
3. You are ready to use it in any Git/GitHub contexts!

## Environment variables for Jenkins configuration

| Name                         | Description                                 |
|------------------------------|---------------------------------------------|
| `CONF_GITHUB_ORG`            | GitHub organization for initial seeded job  |
| `CONF_GITHUB_TOKEN_USER`     | GitHub token's user (e.g. a jenkins user)   |
| `CONF_GITHUB_TOKEN`          | GitHub token                                |
| `CONF_SLACK_TEAM_DOMAIN`     | Slack team's domain, leave empty to disable |
| `CONF_SLACK_TOKEN`           | Slack's Jenkins integration token           |
| `CONF_SLACK_DEFAULT_ROOM`    | Default room to post in Slack               |
| `CONF_JENKINS_URL`           | URL where Jenkins is reachable              |
| `CONF_JENKINS_NUM_EXECUTORS` | Number of executors, default `4`, optional  |

## Shared pipeline libraries

**TODO**

## Future plans

- Use official `digia-factory` image for Jenkins when released

