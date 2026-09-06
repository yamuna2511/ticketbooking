pipeline {
    agent any

    environment {
        DOCKER_HOST = 'tcp://docker:2376'
        DOCKER_TLS_VERIFY = '1'
        DOCKER_CERT_PATH = '/certs/client'
        IMAGE_NAME = 'yamuna2511/ticketbooking'
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

        stage('Version') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME ?: 'unknown'
                    
                    // Make branch name safe for use as a directory name
                    def safeBranchName = branchName.replaceAll('[^a-zA-Z0-9._-]', '-')

                    def versionDir = "/var/jenkins_home/ticketbooking-versions/${safeBranchName}"
                    def versionFile = "${versionDir}/version.txt"

                    sh """
                        mkdir -p '${versionDir}'

                        if [ ! -f '${versionFile}' ]; then
                            echo '1.0' > '${versionFile}'
                        fi
                    """

                    def currentVersion = sh(
                        script: "cat '${versionFile}'",
                        returnStdout: true
                    ).trim()

                    def parts = currentVersion.tokenize('.')
                    def major = parts[0].toInteger()
                    def minor = parts[1].toInteger()

                    minor++

                    def newVersion = "${major}.${minor}"

                    sh """
                        echo '${newVersion}' > '${versionFile}'
                    """

                    env.IMAGE_TAG = newVersion

                    echo "Branch: ${branchName}"
                    echo "Version: ${newVersion}"
                    echo "Version file: ${versionFile}"
                    echo "Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
                }
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
                sh 'docker build -t ticketbooking:${IMAGE_TAG} .'
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

                        docker tag ticketbooking:${IMAGE_TAG} ${IMAGE_NAME}:${IMAGE_TAG}

                        docker push ${IMAGE_NAME}:${IMAGE_TAG}

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
                      ticketbooking=${IMAGE_NAME}:${IMAGE_TAG}

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