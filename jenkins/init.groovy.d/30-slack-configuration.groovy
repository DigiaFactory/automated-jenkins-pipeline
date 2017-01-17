#!/usr/bin/env groovy

// Configure Slack notifications' global settings

import jenkins.model.*
import jenkins.plugins.slack.*

import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret

def env = System.getenv()
def slackDomain = env['CONF_SLACK_TEAM_DOMAIN']

if (!slackDomain) {
  println('INFO: CONF_SLACK_TEAM_DOMAIN not set, assuming Slack should be disabled.')
  return
}

def instance = Jenkins.getInstance()
def store = SystemCredentialsProvider.getInstance().getStore()

def slackCred = new StringCredentialsImpl(
  CredentialsScope.GLOBAL,
  'slack-integration-token',
  'Slack integration token',
  Secret.fromString(env['CONF_SLACK_TOKEN'])
)
SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), slackCred)

def slack = instance.getDescriptorByType(SlackNotifier.DescriptorImpl)
slack.teamDomain = slackDomain
slack.tokenCredentialId = slackCred.id
slack.room = env['CONF_SLACK_DEFAULT_ROOM']
slack.save()

