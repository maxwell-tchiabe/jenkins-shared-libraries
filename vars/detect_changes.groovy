#!/usr/bin/env groovy

/**
 * Detect which components changed between the current and previous commit.
 * Sets environment variables: BACKEND_CHANGED, FRONTEND_CHANGED
 */
def call() {
    def changedFiles = sh(
        script: 'git diff --name-only HEAD~1 HEAD',
        returnStdout: true
    ).trim()

    echo "Changed files:\n${changedFiles}"

    env.BACKEND_CHANGED  = changedFiles.contains('backend/')  ? 'true' : 'false'
    env.FRONTEND_CHANGED = changedFiles.contains('frontend/') ? 'true' : 'false'

    echo "Backend changed: ${env.BACKEND_CHANGED}"
    echo "Frontend changed: ${env.FRONTEND_CHANGED}"
}
