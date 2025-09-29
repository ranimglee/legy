pipeline {
    agent any

     tools {
           jdk 'Java 21'
           maven 'Maven 3.6.3'
       }

       environment {
           JAVA_HOME = "${tool 'Java 21'}"
           PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
           IMAGE_NAME = 'ranimglee/leggy-application'
           SONAR_TOKEN = credentials('sonarqube-token')



       }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh  'mvn clean install -DskipTests'
            }
        }





       stage('Test with Coverage') {
         steps {
           sh 'mvn test'
         }
       }


 stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') { // Le nom de ton serveur SonarQube configuré
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=legy \
                          -Dsonar.host.url=http://sonarqube:9000 \
                          -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }


       stage('Push to DockerHub') {
           steps {
               withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                   sh '''
                       echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                       docker push ${IMAGE_NAME}:latest
                       docker logout
                   '''
               }
           }
       }


        stage('Run Dependencies') {
                 steps {
                     sh '''
                       docker compose down
                       docker compose up -d
                     '''
                 }
             }


        stage('Run App Container') {
            steps {
                sh 'docker ps'
            }
        }
         stage('Health Check - Prometheus') {
                    steps {
                        sh '''
                          for i in {1..10}; do
                            curl --fail http://localhost:9090/ || (echo "Waiting for Prometheus..." && sleep 5)
                            [ $? -eq 0 ] && break
                          done
                        '''
                    }
                }

                // Nouveau stage pour vérifier que Grafana est up
                stage('Health Check - Grafana') {
                    steps {
                        sh '''
                          for i in {1..10}; do
                            curl --fail http://localhost:3000/api/health || (echo "Waiting for Grafana..." && sleep 5)
                            [ $? -eq 0 ] && break
                          done
                        '''
                    }
                }


        stage('Health Check') {
            steps {
                sh '''
                  for i in {1..10}; do
                    curl --fail http://localhost:8084/actuator/health && break
                    echo "Waiting for app to be ready..."
                    sleep 5
                  done
                '''

            }
        }

    }

    post {

        failure {
            echo '❌ Pipeline failed!'
        }
        success {
            echo '✅ Pipeline completed successfully!'
        }
    }
}
