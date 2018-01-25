// intentionally very simple, not relying on any shared structure
node {
    stage 'Checkout', {
        checkout scm
    }
    
    stage 'Tests', {
        sh './gradlew test'
        step([$class: "JUnitResultArchiver", testResults: 'build/test-results/*.xml'])
    }
}

