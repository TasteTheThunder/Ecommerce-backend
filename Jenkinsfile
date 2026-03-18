pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "tastethethunder/sb-ecom"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git 'https://github.com/TasteTheThunder/Ecommerce-backend.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package -DskipTests'
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
                sh '''
                # Apply infra (NO secret.yaml)
                kubectl apply -f k8s/postgres-config.yaml
                kubectl apply -f k8s/configmap.yaml
                kubectl apply -f k8s/postgres-deployment.yaml
                kubectl apply -f k8s/postgres-service.yaml
                kubectl apply -f k8s/app-deployment.yaml
                kubectl apply -f k8s/app-service.yaml

                # Rolling update
                kubectl set image deployment/sb-ecom sb-ecom=$DOCKER_IMAGE:${BUILD_NUMBER} || true
                '''
            }
        }
    }
}