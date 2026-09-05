pipeline {
    agent any
	
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