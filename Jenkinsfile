pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "tastethethunder/sb-ecom"
    }

    stages {

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t $DOCKER_IMAGE:${BUILD_NUMBER} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push $DOCKER_IMAGE:${BUILD_NUMBER}"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                kubectl set image deployment/sb-ecom \
                sb-ecom=$DOCKER_IMAGE:${BUILD_NUMBER}
                """
            }
        }
    }
}