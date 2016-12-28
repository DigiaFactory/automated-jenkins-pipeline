# Pre-configured Jenkins Docker image (WIP)

**NOTE:** This is based on [DigiaCloudServices/digia-factory build-pipeline-in-a-minute/jenkins](https://github.com/DigiaCloudServices/digia-factory)
and will extend that base image when it is finished.

## Usage

**NOTE: Work in progress!**

1. Start container: `docker-compose up -d`
2. Get the initial admin code from the logs: `docker-compose logs` (will be there after a minute or two)
3. Go to `[myaddress]:8080` (e.g. `http://localhost:8080` when running locally) and login using the initial admin code
4. Skip the setup wizard (or select not to install any plugins), all required plugins are pre-installed
5. Start configuring your jobs!

### GitHub access without webhooks

When your Jenkins installation is not accessible from GitHub,
you need to setup SCM polling and use a personal access token
(or similar login method).

**OPTIONAL:** Create a separate user in GitHub for Jenkins to use

1. Create a new personal access token in GitHub
  - **TODO:** Required permissions
2. In Jenkins, create a new set or user-password-credentials,
where username is the GitHub username and password is the access token
  - **TODO:** Document precise steps
3. You are ready to use it in any Git/GitHub contexts!

