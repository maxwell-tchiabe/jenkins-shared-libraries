#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageTag      = config.imageTag      ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName   = config.gitUserName   ?: 'Jenkins CI'
    def gitUserEmail  = config.gitUserEmail  ?: 'jenkins@example.com'
    // Nouveaux paramètres : quels composants ont changé ?
    def updateBackend  = config.updateBackend  ?: false
    def updateFrontend = config.updateFrontend ?: false

    if (!updateBackend && !updateFrontend) {
        echo "No component changed — skipping Kubernetes manifest update."
        return
    }

    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    echo "Update backend: ${updateBackend} | Update frontend: ${updateFrontend}"

    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """

        sh """
            ${updateBackend ? "sed -i 's|image: loicmaxwell/fined-mentor-backend:.*|image: loicmaxwell/fined-mentor-backend:${imageTag}|g' ${manifestsPath}/fined-mentor-backend.yaml" : "echo 'Backend unchanged, skipping manifest update'"}

            ${updateFrontend ? "if [ -f '${manifestsPath}/fined-mentor-frontend.yaml' ]; then sed -i 's|image: loicmaxwell/fined-mentor-frontend:.*|image: loicmaxwell/fined-mentor-frontend:${imageTag}|g' ${manifestsPath}/fined-mentor-frontend.yaml; fi" : "echo 'Frontend unchanged, skipping manifest update'"}

            if git diff --quiet; then
                echo "No changes to commit"
            else
                git add ${manifestsPath}/*.yaml
                git commit -m "Update image tags to ${imageTag} [ci skip]"
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/maxwell-tchiabe/fined-mentor.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}
