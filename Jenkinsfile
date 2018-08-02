#!/usr/bin/env groovy

node('devx-slave-shared') {
    checkout scm

    def version = ""
    def triggerCron = (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') ? '0 */2 * * *' : ''

    name = "alexander-integration-tests"

    //Maven Properties
    def mvnHome = tool name: 'Maven 3', type: 'hudson.tasks.Maven$MavenInstallation'
    def jdkHome = tool name: 'Java 8', type: 'hudson.model.JDK'

    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']],
                pipelineTriggers([cron(triggerCron)])
                ]);

    withCredentials([
        [
          $class: 'UsernamePasswordMultiBinding',
          credentialsId: 'nexus',
          passwordVariable: 'NEXUS_PASS',
          usernameVariable: 'NEXUS_USER'
        ],
        [
          $class: 'UsernamePasswordMultiBinding',
          credentialsId: 'nexus-public',
          passwordVariable: 'PUB_NEXUS_PASS',
          usernameVariable: 'PUB_NEXUS_USER'
        ]
      ]) {

        stage("Checkout") {
          checkout scm
        }

        stage("Calculate build version") {
          version = sh(script: "git describe --tags --always", returnStdout: true).trim()
        }

        stage("Set Maven configuration") {
          env.MAVEN_CUSTOM_GOALS = "javadoc:javadoc install"
          env.MAVEN_CUSTOM_OPTS = "clean checkstyle:checkstyle -DskipTests"
          env.MAVEN_SETTINGS_PATH = '.jenkins/settings.xml'

        }

        stage("Build With No Tests") {
          withEnv(["PATH=${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"]) {
            sh "mvn -s ${env.MAVEN_SETTINGS_PATH} -U -Daether.connector.resumeDownloads=false clean install \
            -DENABLE_USER_SETTINGS_PATH=TRUE \
            -DskipTests=true"
          }
        }

        stage("Run Integration Tests") {
          withEnv(["PATH=${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"]) {
            sh "mvn -s ${env.MAVEN_SETTINGS_PATH} -U -Daether.connector.resumeDownloads=false verify \
            -DENABLE_USER_SETTINGS_PATH=TRUE \
            -Pintegration-tests \
            -Dmozart.environment=devx"
          }
        }

        stage("Deploy Binaries") {
          withEnv(["PATH=${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"]) {
            sh "mvn -s ${env.MAVEN_SETTINGS_PATH} -U -Daether.connector.resumeDownloads=false deploy \
            -DENABLE_USER_SETTINGS_PATH=TRUE"
          }
        }

        stage("Test Reports") {
            junit '**/target/surefire-reports/*.xml'
            junit '**/target/failsafe-reports/*.xml'
        }
    }
}
