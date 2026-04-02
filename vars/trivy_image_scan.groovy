def call(Map config = [:]) {
    def imageName = config.imageName
    def cacheDir = config.cacheDir ?: "${env.WORKSPACE}/.trivy-cache"
    def reportDir = "${env.WORKSPACE}/reports"
    
    if (!imageName) {
        error "Image name is required for trivy_image_scan"
    }
    
    sh "mkdir -p ${cacheDir} ${reportDir}"
    
    def imageSafeName = imageName.replace('/', '_').replace(':', '_')
    
    // Scanner l'image Docker
    sh """
    trivy image \
        --cache-dir ${cacheDir} \
        --format json \
        --output ${reportDir}/trivy-image-${imageSafeName}.json \
        --scanners vuln,secret,misconfig \
        ${imageName}
    """
    
    sh """
    trivy image \
        --cache-dir ${cacheDir} \
        --format table \
        --output ${reportDir}/trivy-image-${imageSafeName}.html \
        --scanners vuln,secret,misconfig \
        ${imageName}
    """
    
    sh """
    trivy image \
        --cache-dir ${cacheDir} \
        --format cyclonedx \
        --output ${reportDir}/sbom-${imageSafeName}.json \
        ${imageName}
    """
    
    echo "Trivy image scan completed for ${imageName}. Reports saved in ${reportDir}/"
    
    def scanResult = sh(
        script: "trivy image --cache-dir ${cacheDir} --format json --severity CRITICAL,HIGH ${imageName} | jq '.Results[] | select(.Vulnerabilities != null) | .Vulnerabilities[] | select(.Severity == \"CRITICAL\" or .Severity == \"HIGH\")'",
        returnStatus: true
    )
    
    if (scanResult == 0) {
        error "High or Critical vulnerabilities found in ${imageName}. Please check the reports."
    }
}
