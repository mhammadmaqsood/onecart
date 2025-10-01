pipeline {
  agent any
  options { timestamps() }

  environment {
    SERVICE      = 'auth-service'
    NAMESPACE    = 'hammadmaqsood135-dev'
    OCP_API_URL  = 'https://api.rm3.7wse.p1.openshiftapps.com:6443'
    OC_BIN       = "${WORKSPACE}/.oc/oc"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Gradle Test & Package') {
      steps {
        sh 'chmod +x ./gradlew || true'
        sh "./gradlew :${SERVICE}:clean :${SERVICE}:test :${SERVICE}:bootJar --no-daemon"
      }
      post {
        always { junit testResults: "${SERVICE}/build/test-results/test/*.xml", allowEmptyResults: true }
      }
    }

    stage('Install oc CLI') {
      steps {
        sh '''
          set -euxo pipefail
          if [ ! -x "$OC_BIN" ]; then
            mkdir -p "$(dirname "$OC_BIN")"
            curl -L -o oc.tar.gz https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/openshift-client-linux.tar.gz
            tar -xzf oc.tar.gz oc
            mv oc "$OC_BIN"
            chmod +x "$OC_BIN"
          fi
          "$OC_BIN" version --client
        '''
      }
    }

    stage('oc login') {
      steps {
        withCredentials([string(credentialsId: 'ocp-token', variable: 'OCP_TOKEN')]) {
          sh '"$OC_BIN" login "$OCP_API_URL" --token="$OCP_TOKEN"'
        }
      }
    }

    stage('Apply Manifests') {
      steps {
        sh '"$OC_BIN" apply -f k8s/openshift/auth-service/buildconfig.yaml -n "$NAMESPACE"'
        sh '"$OC_BIN" apply -f k8s/openshift/auth-service/deployment.yaml -n "$NAMESPACE"'
      }
    }

    stage('Build Image in OpenShift') {
      steps {
        sh '''
          set -euxo pipefail
          "$OC_BIN" start-build auth-service -n "$NAMESPACE" --from-dir=. --wait --follow
        '''
      }
    }

    stage('Deploy & Rollout') {
      steps {
        sh '"$OC_BIN" rollout restart deployment/auth-service -n "$NAMESPACE" || true'
        sh '"$OC_BIN" rollout status deployment/auth-service -n "$NAMESPACE" --timeout=5m'
      }
    }
  }

  post {
    always { archiveArtifacts artifacts: "${SERVICE}/build/libs/*.jar", allowEmptyArchive: true }
  }
}