pipeline {
    agent any

    environment {
        // 🔴 Your DockerHub repo (already correct)
        DOCKER_IMAGE = "tastethethunder/sb-ecom"
    }

    stages {

        stage('Checkout Code') {
            steps {
                // 🔴 Your GitHub repo (already correct)
                git 'https://github.com/TasteTheThunder/Ecommerce-backend.git'
            }
        }

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
                // 🔴 Works because you already did `docker login`
                sh "docker push $DOCKER_IMAGE:${BUILD_NUMBER}"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                // 🔴 MUST match your deployment.yaml
                kubectl set image deployment/sb-ecom \
                sb-ecom=$DOCKER_IMAGE:${BUILD_NUMBER}
                """
            }
        }
    }
}