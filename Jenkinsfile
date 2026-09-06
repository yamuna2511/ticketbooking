pipeline {
    agent any

    environment {
        DOCKER_HOST = 'tcp://docker:2376'
        DOCKER_TLS_VERIFY = '1'
        DOCKER_CERT_PATH = '/certs/client'

        IMAGE_NAME = 'yamuna2511/ticketbooking'

        VERSION_BASE_DIR = '/var/jenkins_home/ticketbooking-versions'
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

                    // Convert feature/test into a safe directory name
                    def safeBranchName =
                        branchName.replaceAll('[^a-zA-Z0-9._-]', '-')

                    def versionDir =
                        "${env.VERSION_BASE_DIR}/${safeBranchName}"

                    def jenkinsVersionFile =
                        "${versionDir}/version.txt"

                    env.VERSION_DIR = versionDir
                    env.JENKINS_VERSION_FILE = jenkinsVersionFile

                    sh """
                        mkdir -p '${versionDir}'
                    """

                    /*
                     * Check whether Jenkins already has a version
                     */
                    def jenkinsVersionExists = sh(
                        script: "[ -f '${jenkinsVersionFile}' ]",
                        returnStatus: true
                    ) == 0

                    if (jenkinsVersionExists) {

                        /*
                         * Jenkins version exists.
                         * Use it for this build.
                         */
                        def currentVersion = sh(
                            script: "cat '${jenkinsVersionFile}'",
                            returnStdout: true
                        ).trim()

                        env.IMAGE_TAG = currentVersion

                        echo "======================================"
                        echo "Jenkins version exists"
                        echo "Branch          : ${branchName}"
                        echo "Jenkins version : ${currentVersion}"
                        echo "Using version   : ${currentVersion}"
                        echo "======================================"

                    } else {

                        /*
                         * Jenkins version does NOT exist.
                         * This is the first build.
                         *
                         * Get the initial version from Git repository.
                         */
                        def repoVersionFile = 'version.txt'

                        def repoVersionExists = sh(
                            script: "[ -f '${repoVersionFile}' ]",
                            returnStatus: true
                        ) == 0

                        if (!repoVersionExists) {
                            error(
                                "First build requires version.txt in the Git repository, but it was not found."
                            )
                        }

                        def repoVersion = sh(
                            script: "cat '${repoVersionFile}'",
                            returnStdout: true
                        ).trim()

                        env.IMAGE_TAG = repoVersion

                        echo "======================================"
                        echo "FIRST BUILD"
                        echo "Branch          : ${branchName}"
                        echo "Jenkins version : NOT FOUND"
                        echo "Repo version    : ${repoVersion}"
                        echo "Using version   : ${repoVersion}"
                        echo "======================================"

                    }

                    echo "Docker image will use:"
                    echo "${env.IMAGE_NAME}:${env.IMAGE_TAG}"
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
            when {
                anyOf {
                    branch 'main'
                    branch 'release'
                    branch 'patchset'
                    branch 'hotfix'
                }
            }

            steps {
                sh '''
                    docker build \
                        -t ticketbooking:${IMAGE_TAG} \
                        .
                '''
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'release'
                    branch 'patchset'
                    branch 'hotfix'
                }
            }

            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASSWORD" | \
                            docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        docker tag \
                            ticketbooking:${IMAGE_TAG} \
                            ${IMAGE_NAME}:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_NAME}:${IMAGE_TAG}

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when {
                anyOf {
                    branch 'main'
                    branch 'release'
                    branch 'patchset'
                    branch 'hotfix'
                }
            }

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

        /*
         * IMPORTANT:
         *
         * This stage runs only after all previous stages succeed.
         *
         * Therefore:
         * - Build failure     -> no increment
         * - Test failure      -> no increment
         * - Docker failure    -> no increment
         * - Push failure      -> no increment
         * - Deployment fail   -> no increment
         * - Everything passes -> increment
         */
        stage('Update Jenkins Version') {
            when {
                anyOf {
                    branch 'main'
                    branch 'release'
                    branch 'patchset'
                    branch 'hotfix'
                }
            }

            steps {
                script {

                    def currentVersion = env.IMAGE_TAG

                    def parts = currentVersion.tokenize('.')

                    if (parts.size() != 2) {
                        error(
                            "Invalid version '${currentVersion}'. Expected format like 1.0"
                        )
                    }

                    def major = parts[0].toInteger()
                    def minor = parts[1].toInteger()

                    def nextVersion = "${major}.${minor + 1}"

                    sh """
                        echo '${nextVersion}' > '${env.JENKINS_VERSION_FILE}'
                    """

                    echo "======================================"
                    echo "DEPLOYMENT SUCCESSFUL"
                    echo "Branch         : ${env.BRANCH_NAME}"
                    echo "Used version   : ${currentVersion}"
                    echo "Next version   : ${nextVersion}"
                    echo "Jenkins file   : ${env.JENKINS_VERSION_FILE}"
                    echo "======================================"
                }
            }
        }
    }

    post {

        success {
            echo 'Jenkins pipeline completed successfully!'
        }

        failure {
            echo 'Jenkins pipeline failed. Jenkins version was NOT incremented.'
        }
    }
}