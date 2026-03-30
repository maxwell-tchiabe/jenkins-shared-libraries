def call() {
    echo "Running unit tests..."
    
    // Add your unit test commands here
    // For example:
    sh "mvn -f backend/pom.xml test -DskipTests"    
   // echo "Unit tests completed successfully"
}
