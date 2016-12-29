# Automated Jenkins CI stack

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

See `jenkins/README.md`

**TODO: Docker commands and other info**

## Future plans

- Use official `digia-factory` image for Jenkins when released
- Include Nexus or similar for Docker/npm/other registry (`digia-factory/nexus`)
- Support GitLab and/or Gogs
