pipeline {
    agent any
    environment {
        DOCKER_COMPOSE_VERSION = '1.25.0' // 사용할 Docker Compose의 버전
        GITLAB_TOKEN = credentials('wns1915') // Jenkins에 저장된 GitLab Token의 ID
    }
    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'wns1915', url: 'https://lab.ssafy.com/wns1915/oringe.git' // GitLab 리포지토리
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker-compose -f devway/docker-compose.yml up -d app'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    sh 'docker-compose -f devway/docker-compose.yml up -d nginx'
                }
            }
        }
    }
    post {
        always {
            cleanWs() // 작업 공간 정리
        }
    }
}
