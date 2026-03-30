def call(Map config = [:]) {
    def projectKey   = config.projectKey   ?: 'fined-mentor'
    def projectName  = config.projectName  ?: 'Fined Mentor'
    def sonarServer  = config.sonarServer  ?: 'sonarqube'      // matches Jenkins config name
    def sonarToken   = config.sonarToken   ?: 'sonarqube-token' // Jenkins credential ID

    withSonarQubeEnv(sonarServer) {
        withCredentials([string(credentialsId: sonarToken, variable: 'SONAR_TOKEN')]) {
            sh """
                cd backend && mvn sonar:sonar \
                  -Dsonar.projectKey=${projectKey} \
                  -Dsonar.projectName='${projectName}' \
                  -Dsonar.host.url=\${SONAR_HOST_URL} \
                  -Dsonar.token=\${SONAR_TOKEN}
            """
        }
    }
}
