def call(Map params = [:]) {
    String targetUrl = params.targetUrl
    String reportName = params.reportName ?: 'zap-report.html'
    String reportDir = params.reportDir ?: 'reports'
    boolean ignoreFailures = params.containsKey('ignoreFailures') ? params.ignoreFailures : true
    String zapImage = params.zapImage ?: 'zaproxy/zap-stable'
    if (!targetUrl) {
        error "runZapBaselineScan: 'targetUrl' parameter is required."
    }
    echo "Running OWASP ZAP Baseline Scan against ${targetUrl}..."
    
    // Ensure the reports directory exists and is writable by the docker container
    sh "mkdir -p \${WORKSPACE}/${reportDir} && chmod 777 \${WORKSPACE}/${reportDir}"
    
    String ignoreFlag = ignoreFailures ? '-I' : ''
    
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        sh """
        docker run --rm -v \${WORKSPACE}/${reportDir}:/zap/wrk/:rw -t ${zapImage} zap-baseline.py \
            -t ${targetUrl} \
            -r ${reportName} \
            ${ignoreFlag}
        """
    }
}
