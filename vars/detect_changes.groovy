#!/usr/bin/env groovy

/**
 * Detect which components changed between the current and previous commit.
 * Sets environment variables: BACKEND_CHANGED, FRONTEND_CHANGED
 */
def call() {
    // Use shell grep directly — more reliable than Groovy string comparison
    // grep -q exits 0 if match found, 1 if not. "|| true" prevents stage failure.
    def backendChanged = sh(
        script: 'git diff --name-only HEAD~1 HEAD | grep -q "^backend/" && echo "true" || echo "false"',
        returnStdout: true
    ).trim()

    def frontendChanged = sh(
        script: 'git diff --name-only HEAD~1 HEAD | grep -q "^frontend/" && echo "true" || echo "false"',
        returnStdout: true
    ).trim()

    env.BACKEND_CHANGED  = backendChanged
    env.FRONTEND_CHANGED = frontendChanged

    // Log for debugging
    sh 'echo "=== Changed files ==="; git diff --name-only HEAD~1 HEAD'
    echo "Backend changed: ${env.BACKEND_CHANGED}"
    echo "Frontend changed: ${env.FRONTEND_CHANGED}"
}
