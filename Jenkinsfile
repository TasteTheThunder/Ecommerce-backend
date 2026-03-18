pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "tastethethunder/sb-ecom"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
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

        // 🔥 NEW STAGE (IMPORTANT)
        stage('Create Kubernetes Secret') {
            steps {
                withCredentials([
                    string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                    string(credentialsId: 'STRIPE_SECRET_KEY', variable: 'STRIPE_SECRET_KEY'),
                    string(credentialsId: 'POSTGRES_PASSWORD', variable: 'POSTGRES_PASSWORD')
                ]) {
                    sh '''
                    kubectl create secret generic sb-ecom-secret \
                    --from-literal=DB_PASSWORD=$DB_PASSWORD \
                    --from-literal=JWT_SECRET=$JWT_SECRET \
                    --from-literal=STRIPE_SECRET_KEY=$STRIPE_SECRET_KEY \
                    --from-literal=POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
                    --dry-run=client -o yaml | kubectl apply -f -
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                kubectl apply -f k8s/postgres-config.yaml
                kubectl apply -f k8s/configmap.yaml
                kubectl apply -f k8s/postgres-deployment.yaml
                kubectl apply -f k8s/postgres-service.yaml
                kubectl apply -f k8s/app-deployment.yaml
                kubectl apply -f k8s/app-service.yaml

                kubectl set image deployment/sb-ecom sb-ecom=$DOCKER_IMAGE:${BUILD_NUMBER} || true
                '''
            }
        }
    }
}