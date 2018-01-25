// intentionally very simple, not relying on any shared structure
pipeline {
    stage 'Checkout', {
        checkout scm
    }
    
    stage 'Tests', {
        sh 'gradle test'
    }
}

