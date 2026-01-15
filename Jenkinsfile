pipeline {
    agent any

    environment {
        COMPOSE_FILE = 'docker-compose.yaml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

       stage('Prepare Environment') {
           steps {
               withCredentials([file(credentialsId: 'my-app-env', variable: 'ENV_FILE')]) {

                   sh 'cp \$ENV_FILE .env'
               }
           }
       }
        stage('Start Infrastructure') {
            steps {
                echo "Starting Databases and Kafka..."
                sh "docker compose -f ${COMPOSE_FILE} up -d --wait postgres-order postgres-payment postgres-warehouse kafka"
            }
        }

        stage('Run Tests') {
            parallel {
                stage('Order Service') {
                    steps {
                        sh "docker compose -f ${COMPOSE_FILE} run --rm --build --no-deps order-platform.order-service ./gradlew test"
                    }
                }
                stage('Payment Service') {
                    steps {
                        sh "docker compose -f ${COMPOSE_FILE} run --rm --build --no-deps order-platform.payment-service ./gradlew test"
                    }
                }
                stage('Warehouse Service') {
                    steps {
                        sh "docker compose -f ${COMPOSE_FILE} run --rm --build --no-deps order-platform.warehouse-service ./gradlew test"
                    }
                }
            }
        }
        stage('Stop Old Containers') {
            steps {
                sh "docker compose -f ${COMPOSE_FILE} down || true"
            }
        }

        stage('Build & Deploy') {
            steps { 
                sh "docker compose -f ${COMPOSE_FILE} up -d --build"
            }
        }

        stage('Cleanup') {
            steps { 
                sh "docker image prune -f"
            }
        }
    }
    
    post {
        failure {
            echo "Build Failed."
        }
        success {
            echo "Build Success"
        }
    }
}