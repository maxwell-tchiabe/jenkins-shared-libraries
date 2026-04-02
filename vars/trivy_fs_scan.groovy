def call(Map config = [:]) {
    def cacheDir = config.cacheDir ?: "${env.WORKSPACE}/.trivy-cache"
    def reportDir = "${env.WORKSPACE}/reports"
    
    sh "mkdir -p ${cacheDir} ${reportDir}"
    
    sh """
    trivy fs \
        --cache-dir ${cacheDir} \
        --format json \
        --output ${reportDir}/trivy-fs.json \
        --scanners vuln,secret,misconfig \
        .
    """
    
    sh """
    trivy fs \
        --cache-dir ${cacheDir} \
        --format table \
        --output ${reportDir}/trivy-fs.html \
        --scanners vuln,secret,misconfig \
        .
    """
    
    sh """
    trivy fs \
        --cache-dir ${cacheDir} \
        --format json \
        --output ${reportDir}/trivy-dependencies.json \
        --scanners vuln \
        .
    """
    
    echo "Trivy filesystem scan completed. Reports saved in ${reportDir}/"
}
