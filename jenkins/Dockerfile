# Heavily based on the official Jenkins image:
# https://github.com/jenkinsci/docker/blob/79e871d7ea9085cabb287dd53705b4432e36cf6f/Dockerfile

FROM openjdk:8-jdk
LABEL maintainer="Mikko Piuhola <mikko.piuhola@digia.com>"

# Jenkins version being bundled in this docker image
ARG JENKINS_VERSION=2.41
# jenkins.war checksum, download will be validated using it
ARG JENKINS_SHA=ed360bd0d720b5b5b78e0af979944779034065d6

# Jenkins settings
ENV JENKINS_HOME=/var/jenkins_home 
ENV JENKINS_SLAVE_AGENT_PORT=50000 \
    JENKINS_UC=https://updates.jenkins.io \
    COPY_REFERENCE_FILE_LOG=${JENKINS_HOME}/copy_reference_file.log \
    TINI_VERSION=0.9.0 \
    TINI_SHA=fa23d1e20732501c3bb8eeeca423c89ac80ed452 \
    DOCKER_VERSION=1.12.5 \
    DOCKER_SHA256=0058867ac46a1eba283e2441b1bb5455df846144f9d9ba079e97655399d4a2c6 \
    JENKINS_OPTS=-Djenkins.install.runSetupWizard=false \
    SEED_JOBS_DIR=/usr/share/jenkins/ref/seed-jobs \
    CONF_GITHUB_TOKEN_ID=github-org-token

# Can be used to customize where jenkins.war get downloaded from
ARG JENKINS_URL=https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war

# Names & IDs for Jenkins user
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

# Install general and build-time dependencies,
# clear apt cache (no need to leave in image, wasting space).
# Jenkins is run with user `jenkins`, uid = 1000
# If you bind mount a volume from the host or a data container,
# ensure you use the same uid.
# `/usr/share/jenkins/ref/` contains all reference configuration we want
# to set on a fresh new installation. Use it to bundle additional plugins
# or config file with your custom jenkins Docker image.
RUN apt-get update && apt-get install -y \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user} \
    && mkdir -p /usr/share/jenkins/ref/init.groovy.d \
    && mkdir -p /usr/share/jenkins/ref/seed-jobs

# Use tini as subreaper in Docker container to adopt zombie processes.
# Next, install Jenkins itself.
# Install Docker CLI to enable usage of Docker inside Jenkins jobs.
RUN curl -fsSL https://github.com/krallin/tini/releases/download/v${TINI_VERSION}/tini-static -o /bin/tini && chmod +x /bin/tini \
    && echo "$TINI_SHA  /bin/tini" | sha1sum -c - \
    && curl -fsSL ${JENKINS_URL} -o /usr/share/jenkins/jenkins.war \
    && echo "${JENKINS_SHA}  /usr/share/jenkins/jenkins.war" | sha1sum -c - \
    && chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref \
    && curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-${DOCKER_VERSION}.tgz \
    && echo "${DOCKER_SHA256}  docker-${DOCKER_VERSION}.tgz" | sha256sum -c - \
    && tar --strip-components=1 -xvzf docker-${DOCKER_VERSION}.tgz -C /usr/local/bin \
    && chmod +rx /usr/local/bin/docker \
    && chmod +s /usr/local/bin/docker

# Jenkins home directory is a volume, so configuration and build history
# can be persisted and survive image upgrades
VOLUME /var/jenkins_home

# Web UI & slave machines:
EXPOSE 8080 50000

# No need to give Jenkins root privileges,
# run as custom user.
USER ${user}

# From a derived Dockerfile one can use `RUN plugins.sh active.txt`
# to setup /usr/share/jenkins/ref/plugins from a support bundle.
COPY scripts/ /usr/local/bin/

# Install the initial plugins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt | tr '\n' ' ')

# Copy after installing to prevent unnecessary
# extra layers from plugin installs.
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
COPY seed-jobs/ /usr/share/jenkins/ref/seed-jobs/

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
