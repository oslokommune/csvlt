@Library('k8s-jenkins-pipeline-library')

import no.ok.build.k8s.jenkins.pipeline.stages.*
import no.ok.build.k8s.jenkins.pipeline.stages.gradle.*
import no.ok.build.k8s.jenkins.pipeline.pipeline.*
import no.ok.build.k8s.jenkins.pipeline.common.*

Closure gradleBody = {
    sh './gradlew --stacktrace --no-daemon build '
}

new Pipeline(this)
        .addStage(new ScmCheckoutStage())
        .addStage(new GradleContainerTemplate("Java build", gradleBody))
        .execute()