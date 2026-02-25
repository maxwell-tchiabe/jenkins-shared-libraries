#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'jenkins@example.com'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        // Configure Git
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """
        
        // Update deployment manifests with new image tags - using proper Linux sed syntax
        sh """
            # Update main application deployment - note the correct image name is loicmaxwell/fined-mentor-backend
            sed -i "s|image: loicmaxwell/fined-mentor-backend:.*|image: loicmaxwell/fined-mentor-backend:${imageTag}|g" ${manifestsPath}/fined-mentor-backend.yaml
            
            # Update main application deployment - note the correct image name is loicmaxwell/fined-mentor-frontend
            if [ -f "${manifestsPath}/fined-mentor-frontend.yaml" ]; then
                sed -i "s|image: loicmaxwell/fined-mentor-frontend:.*|image: loicmaxwell/fined-mentor-frontend:${imageTag}|g" ${manifestsPath}/fined-mentor-frontend.yaml
            fi
            
            # Check for changes
            if git diff --quiet; then
                echo "No changes to commit"
            else
                # Commit and push changes
                git add ${manifestsPath}/*.yaml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                
                # Set up credentials for push
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/maxwell-tchiabe/fined-mentor.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}
