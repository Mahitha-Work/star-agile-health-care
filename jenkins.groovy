
pipeline {
    agent any
    
    stages{
        stage('git checkout'){
            steps{
                echo 'cloning the git repo'
                git 'https://github.com/Mahitha-Work/star-agile-health-care.git'
                
            }
        }
        stage('build the project') {
            steps{
                sh 'mvn clean package'
            }
        }
        stage('create docker image'){
            steps{
                echo 'build the docker image'
                sh 'docker build -t mahithareddy/health-care:1.0 .'
            }
        }
        stage('Push Docker Image') {
            steps {
                echo 'Pushing the Docker '

                withCredentials([usernamePassword(
                    credentialsId: 'dockerhubcreds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push mahithareddy/health-care:1.0
                    '''
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-file', variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                        export KUBECONFIG=$KUBECONFIG_FILE
                        kubectl apply -f kubernetesfile.yml
                    '''
                }
            }
        }
    }
}
