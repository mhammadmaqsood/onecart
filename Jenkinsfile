pipeline {
  agent any
  options { timestamps() }

  parameters {
    choice(name: 'SERVICE', choices: ['auth-service','config-server'], description: 'Which service to build & deploy')
  }

  environment {
    SERVICE      = "${params.SERVICE}"
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
          set -eux
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
        sh '"$OC_BIN" apply -f k8s/openshift/${SERVICE}/buildconfig.yaml -n "$NAMESPACE"'
        sh '"$OC_BIN" apply -f k8s/openshift/${SERVICE}/deployment.yaml -n "$NAMESPACE"'
      }
    }

    stage('Build Image in OpenShift') {
      steps {
        sh '''
          set -eux
          # pick the built jar for the selected module
          JAR_FILE=$(ls ${SERVICE}/build/libs/*.jar | head -n 1)

          # tiny build context with only jar + Dockerfile
          rm -rf build-upload
          mkdir build-upload
          cp "$JAR_FILE" build-upload/app.jar
          cp Dockerfile build-upload/Dockerfile

          # start build (name matches service)
          BUILD_NAME=$("$OC_BIN" start-build "${SERVICE}" -n "$NAMESPACE" --from-dir=build-upload -o name | sed 's#.*/##')
          echo "Started build: $BUILD_NAME"

          # stream logs, then wait up to 15m for completion
          "$OC_BIN" logs -f "build/$BUILD_NAME" -n "$NAMESPACE" || true
          if ! "$OC_BIN" wait --for=condition=Complete "build/$BUILD_NAME" -n "$NAMESPACE" --timeout=15m; then
            echo "Build did not complete. Diagnostics..."
            "$OC_BIN" get build "$BUILD_NAME" -n "$NAMESPACE" -o yaml || true
            POD_NAME=$("$OC_BIN" get pod -n "$NAMESPACE" -l "openshift.io/build.name=$BUILD_NAME" -o jsonpath='{.items[0].metadata.name}' || true)
            if [ -n "${POD_NAME:-}" ]; then
              "$OC_BIN" describe pod "$POD_NAME" -n "$NAMESPACE" || true
              "$OC_BIN" logs "$POD_NAME" -n "$NAMESPACE" --all-containers=true --tail=200 || true
            fi
            exit 1
          fi
        '''
      }
    }

    stage('Deploy & Rollout') {
      steps {
        sh '"$OC_BIN" rollout restart deployment/${SERVICE} -n "$NAMESPACE" || true'
        sh '"$OC_BIN" rollout status deployment/${SERVICE} -n "$NAMESPACE" --timeout=5m'
      }
    }
  }

  post {
    always { archiveArtifacts artifacts: "${SERVICE}/build/libs/*.jar", allowEmptyArchive: true }
  }
}