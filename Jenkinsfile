pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "tastethethunder/sb-ecom"
    }

    stages {

        stage('Build & Test') {
            steps {
                // 🔴 Windows command
                bat 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% ."
            }
        }

        stage('Push Docker Image') {
            steps {
                bat "docker push %DOCKER_IMAGE%:%BUILD_NUMBER%"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                bat """
                kubectl set image deployment/sb-ecom ^
                sb-ecom=%DOCKER_IMAGE%:%BUILD_NUMBER%
                """
            }
        }
    }
}