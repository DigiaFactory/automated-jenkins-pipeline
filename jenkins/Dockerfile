# Heavily based on the official Jenkins image:
# https://github.com/jenkinsci/docker/blob/79e871d7ea9085cabb287dd53705b4432e36cf6f/Dockerfile

FROM openjdk:8-jdk
LABEL maintainer="Mikko Piuhola <mikko.piuhola@digia.com>"

# Jenkins version being bundled in this docker image
ARG JENKINS_VERSION=2.19.3
# jenkins.war checksum, download will be validated using it
ARG JENKINS_SHA=e97670636394092af40cc626f8e07b092105c07b

# Jenkins settings
ENV JENKINS_HOME=/var/jenkins_home 
ENV JENKINS_SLAVE_AGENT_PORT=50000 \
    JENKINS_UC=https://updates.jenkins.io \
    COPY_REFERENCE_FILE_LOG=${JENKINS_HOME}/copy_reference_file.log \
    TINI_VERSION=0.9.0 \
    TINI_SHA=fa23d1e20732501c3bb8eeeca423c89ac80ed452

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
# Use tini as subreaper in Docker container to adopt zombie processes.
# Finally, install Jenkins itself.
RUN apt-get update && apt-get install -y \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g ${gid} ${group} \
    && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user} \
    && mkdir -p /usr/share/jenkins/ref/init.groovy.d \
    && curl -fsSL https://github.com/krallin/tini/releases/download/v${TINI_VERSION}/tini-static -o /bin/tini && chmod +x /bin/tini \
    && echo "$TINI_SHA  /bin/tini" | sha1sum -c - \
    && curl -fsSL ${JENKINS_URL} -o /usr/share/jenkins/jenkins.war \
    && echo "${JENKINS_SHA}  /usr/share/jenkins/jenkins.war" | sha1sum -c - \
    && chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref

# Jenkins home directory is a volume, so configuration and build history
# can be persisted and survive image upgrades
VOLUME /var/jenkins_home

# Web UI & slave machines:
EXPOSE 8080 50000

# No need to give Jenkins root privileges,
# run as custom user.
USER ${user}

# From a derived Dockerfile one can use `RUN plugins.sh active.txt` to setup /usr/share/jenkins/ref/plugins from a support bundle
COPY scripts/ /usr/local/bin/
COPY init.groovy /usr/share/jenkins/ref/init.groovy.d/tcp-slave-agent-port.groovy

# Install the initial plugins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt | tr '\n' ' ')

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
