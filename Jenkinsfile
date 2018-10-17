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
            // jenkins@prod--alfred:~$ ssh -p 16022 elife-alfred-user@localhost who-am-i
            // Authenticated as: elife-alfred-user
            // Authorities:
            //   authenticated

            sh 'git remote add local-jenkins ssh://elife-alfred-user@localhost:16022/workflowLibs.git'
            sh 'git push local-jenkins master'
        }
    }
}

