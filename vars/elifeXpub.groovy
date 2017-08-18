def call()
{
    elifeLibrary {
        def commit
        stage 'Checkout', {
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://gitlab.coko.foundation/yld/xpub']]])    
            commit = elifeGitRevision()
        }
        
        stage 'Trigger dependency update', {
             build job: 'dependencies-elife-xpub-update-xpub', wait: false, parameters: [string(name: 'commit', value: commit)]
        }
    }
}
