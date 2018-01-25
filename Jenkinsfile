// intentionally very simple, not relying on any shared structure
node {
    stage 'Checkout', {
        checkout scm
    }
    
    stage 'Tests', {
        sh './gradlew test'
    }
}

