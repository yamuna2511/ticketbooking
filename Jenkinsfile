pipeline {
    agent any
	
	environment {
        DOCKER_HOST = 'tcp://docker:2376'
        DOCKER_TLS_VERIFY = '1'
        DOCKER_CERT_PATH = '/certs/client'
    }
	
	tools { 
		maven 'Maven' 
	}

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
		
		stage('Docker Build') {
			steps {
				sh 'docker build -t ticketbooking:1.5 .'
			}
		}
		
		stage('Docker Push') {
			steps {
				withCredentials([usernamePassword(
					credentialsId: 'dockerhub-credentials',
					usernameVariable: 'DOCKER_USERNAME',
					passwordVariable: 'DOCKER_PASSWORD'
				)]) {
					sh '''
						echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
						docker tag ticketbooking:1.5 $DOCKER_USERNAME/ticketbooking:1.5
						docker push $DOCKER_USERNAME/ticketbooking:1.5
						docker logout
					'''
				}
			}
		}
		
		stage('Deploy to Kubernetes') {
			steps {
				sh '''
					kubectl \
					  --kubeconfig=/tmp/jenkins-kubeconfig \
					  --server=https://host.docker.internal:51396 \
					  --tls-server-name=localhost \
					  -n development \
					  set image deployment/ticketbooking \
					  ticketbooking=yamuna2511/ticketbooking:1.5

					kubectl \
					  --kubeconfig=/tmp/jenkins-kubeconfig \
					  --server=https://host.docker.internal:51396 \
					  --tls-server-name=localhost \
					  -n development \
					  rollout status deployment/ticketbooking
				'''
			}
		}
    }

    post {
        success {
            echo 'Jenkins pipeline completed successfully!'
        }

        failure {
            echo 'Jenkins pipeline failed. Check the console output.'
        }
    }
}