# Pre-configured Jenkins Docker image (WIP)

**NOTE:** This is based on [DigiaCloudServices/digia-factory build-pipeline-in-a-minute/jenkins](https://github.com/DigiaCloudServices/digia-factory)
and will extend that base image when it is finished.

## Usage

See the main README

### Initial configuration

Jenkins can be configured via Groovy scripts. Add any configuration scripts to `init.groovy.d`
and they will be executed on initial boot.

Some configuration is provided for the purposes of this automated stack, like a way to
provide GitHub credentials.

