#!/usr/bin/env groovy

name = "alexander-integration-tests"

final ANYPOINT_ENVIRONMENT = 'ANYPOINT_ENVIRONMENT'

final DEFAULT_PIPELINE_ENV = 'kdev'

def AGENT_LABEL = ""

def pipelineEnv = ""

def triggerCron = '0 */2 * * *'

node {

    //Maven Properties
    def mvnHome = tool name: 'Maven 3', type: 'hudson.tasks.Maven$MavenInstallation'
    def jdkHome = tool name: 'Java 8', type: 'hudson.model.JDK'


    properties([
                    [
                        $class: 'BuildDiscarderProperty',
                        strategy: [
                            $class: 'LogRotator',
                            artifactDaysToKeepStr: '',
                            artifactNumToKeepStr: '',
                            daysToKeepStr: '',
                            numToKeepStr: '10'
                        ]
                    ],
                    pipelineTriggers([cron(triggerCron)]),
                    parameters([
                                string(
                                    name: ANYPOINT_ENVIRONMENT,
                                    description: 'Environment to be tested',
                                    defaultValue: DEFAULT_PIPELINE_ENV
                                ),
                    ])
                ]);

    stage('Check parameters') {
        echo "environment by param: ${params.ANYPOINT_ENVIRONMENT}"
        pipelineEnv = params.ANYPOINT_ENVIRONMENT ?: DEFAULT_PIPELINE_ENV
        AGENT_LABEL = "${pipelineEnv}-slave-shared"

        echo "Using agent ${AGENT_LABEL}"
    }

}

node("${AGENT_LABEL}") {

properties([
                    [
                        $class: 'BuildDiscarderProperty',
                        strategy: [
                            $class: 'LogRotator',
                            artifactDaysToKeepStr: '',
                            artifactNumToKeepStr: '',
                            daysToKeepStr: '',
                            numToKeepStr: '10'
                        ]
                    ],
                    pipelineTriggers([cron(triggerCron)]),
                    parameters([
                                string(
                                    name: ANYPOINT_ENVIRONMENT,
                                    description: 'Environment to be tested',
                                    defaultValue: DEFAULT_PIPELINE_ENV
                                ),
                    ])
                ]);

    checkout scm

    def version = ""
    triggerCron = (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') ? '0 */2 * * *' : ''

    name = "alexander-integration-tests"

    //Maven Properties
    def mvnHome = tool name: 'Maven 3', type: 'hudson.tasks.Maven$MavenInstallation'
    def jdkHome = tool name: 'Java 8', type: 'hudson.model.JDK'

    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']],
                pipelineTriggers([cron(triggerCron)]),
                 parameters([
                             string(
                                 name: ANYPOINT_ENVIRONMENT,
                                 description: 'Environment to be tested',
                                 defaultValue: DEFAULT_PIPELINE_ENV
                             ),
                 ])
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
            -Dmozart.environment=${pipelineEnv}"
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
        }
    }
}
