import java.nio.file.Path
import java.nio.file.Files

pipeline {
    agent any
    stages {
        stage("Build") {
            steps {
                sh "./gradlew clean build"
                junit testResults: "build/test-results/*Test/*.xml"
                archiveArtifacts artifacts: 'build/libs/*.jar,build/libs/*.klib,benchmark/build/distributions/*.jar',
                                 allowEmptyArchive: true,
                                 fingerprint: true,
                                 onlyIfSuccessful: true
            }
        }
        stage("Publish") {
            steps {
                sh "./gradlew publish"
            }
        }
    }
}

