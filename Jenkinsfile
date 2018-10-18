// intentionally very simple, not relying on any shared structure
node {
    stage 'Checkout', {
        checkout scm
    }
    
    stage 'Tests', {
        sh './gradlew test'
        step([$class: "JUnitResultArchiver", testResults: 'build/test-results/*.xml'])
    }

    elifeMainlineOnly {
        stage 'Push to local Jenkins', {
            // relies on elife-alfred-user public key configured at https://alfred.elifesciences.org/me/configure after logging in with that Github user
            // jenkins@prod--alfred:~$ ssh -p 16022 elife-alfred-user@localhost who-am-i
            sh './push-to-jenkins.sh elife-alfred-user localhost 16022'
        }
    }
}

