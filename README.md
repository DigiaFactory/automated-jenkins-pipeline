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

#### Environment variables for Jenkins configuration

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

### Creating an access token

**OPTIONAL:** Create a separate user in GitHub for Jenkins to use

1. Create a new personal access token in GitHub
  - `https://github.com/settings/tokens/new?scopes=repo,public_repo,admin:repo_hook,admin:org_hook&description=Jenkins+Access`
2. Provide the GitHub configurations for Docker (see above)
3. You are ready to use it in any Git/GitHub contexts!

### Steps

1. Start all services: `docker-compose up -d`
2. Get the initial admin code for Jenkins from the logs:
`docker-compose logs` (will be there after a minute or two)
3. Go to `[myaddress]:8080` (e.g. `http://localhost:8080` when running locally)
and login using the initial admin code
4. Skip the setup wizard (or select not to install any plugins),
all required plugins are pre-installed
5. Create an access token (see below)
6. Setup GitHub access (see below, with or without webhooks)

### GitHub access with webhooks

Prerequirements:
- Your Jenkins server is accessible from GitHub
  - See official GitHub documentation for webhooks

1. In Jenkins dashboard, create `New Item`
2. Give it the name of your organization (or whatever you like)
and select "GitHub Organization" as the type
3. Under `Project Sources > Repository Sources`, add a new `GitHub Organization`
where the `Owner` is your organization name
  - Optionally, select a selection pattern for your repositories,
  to white-/blaclist repositories
4. Select a valid set of credentials to access your repository
5. Under `Project Sources > Project Recognizers`, add `Pipeline Jenkinsfile`
6. Save

**TODO:** Shared Pipeline libraries

### GitHub access without webhooks

When your Jenkins installation is not accessible from GitHub,
you need to use SCM polling and use a personal access token
(or similar login method).

## Future plans

- Use official `digia-factory` image for Jenkins when released
- Include Nexus or similar for Docker/npm/other registry (`digia-factory/nexus`)

