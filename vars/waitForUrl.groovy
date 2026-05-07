def call(Map params = [:]) {
    String url = params.url
    int timeoutMinutes = params.timeoutMinutes ?: 5
    int intervalSeconds = params.intervalSeconds ?: 15
    String expectedStatus = params.expectedStatus ?: '200'
    int bufferSeconds = params.bufferSeconds ?: 15
    if (!url) {
        error "waitForUrl: 'url' parameter is required."
    }
    echo "Polling URL ${url} with curl to verify deployment..."
    
    timeout(time: timeoutMinutes, unit: 'MINUTES') {
        waitUntil {
            def statusCode = sh(
                script: "curl -s -o /dev/null -w \"%{http_code}\" ${url} || echo \"000\"", 
                returnStdout: true
            ).trim()
            
            if (statusCode == expectedStatus) {
                echo "Environment at ${url} is reachable (HTTP ${expectedStatus})."
                return true
            } else {
                echo "Waiting for environment to become ready (current status: ${statusCode})..."
                sleep time: intervalSeconds, unit: 'SECONDS'
                return false
            }
        }
    }
    
    if (bufferSeconds > 0) {
        echo "Applying a buffer of ${bufferSeconds} seconds to ensure traffic routing is stable..."
        sleep time: bufferSeconds, unit: 'SECONDS'
    }
}
