pipeline {
    agent any
    environment {
        DOCKER_COMPOSE_VERSION = '1.25.0' // 사용할 Docker Compose의 버전
        GITLAB_TOKEN = credentials('wns1915') // Jenkins에 저장된 GitLab Token의 ID
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'release', credentialsId: 'wns1915', url: 'https://lab.ssafy.com/wns1915/oringe.git' // GitLab 리포지토리
            }
        }
		stage('Update Local Repository') {
            steps {
                script {
						withCredentials([usernamePassword(credentialsId: 'wns1915', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
							sh '''
							ENCODED_USERNAME=$(echo $GIT_USERNAME | sed 's/@/%40/g')
							cd /home/ubuntu/oringe
							git pull https://$ENCODED_USERNAME:$GIT_PASSWORD@lab.ssafy.com/wns1915/oringe.git release
							'''
						}
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml build --no-cache app'
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml up -d app'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
					sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml build --no-cache nginx '
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml up -d nginx '
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml up -d certbot'
                }
            }
        }
    }
}
