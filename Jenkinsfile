pipeline {
    agent any
    tools {
        gradle 'gradle'
    }
    environment {
        DOCKER_IMAGE = 'daneker/co-assemble-api'
    }
    stages {
        stage('Build project') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                checkout scmGit(branches: [[name: '*/${env.BRANCH_NAME}']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/youngAndMad/co-assemble-api.git']])
                sh 'gradle clean build'
            }
        }
        stage('Build docker image') {
            when {
                branch 'test'
            }
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:test ."
            }
        }
       stage('Manual Release') {
           when {
               branch 'master'
           }
           steps {
               script {
                   input(message: 'Release to production?')
               }
               checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/youngAndMad/co-assemble-api.git']])
               sh 'gradle clean build'
               // Capture the version from Gradle
               script {
                   env.VERSION = sh(script: "gradle -q printVersion", returnStdout: true).trim()
               }
               sh 'gradle release'
               withCredentials([string(credentialsId: 'DOCKER_PASSWORD', variable: 'DOCKER_PASSWORD')]) {
                   sh "docker login -u daneker -p ${DOCKER_PASSWORD}"
                   // Build and push the Docker image with the version tag
                   sh "docker build -t ${DOCKER_IMAGE}:${env.VERSION} ."
                   sh "docker push ${DOCKER_IMAGE}:${env.VERSION}"
               }
           }
       }
    }
}
