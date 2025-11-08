pipeline {
  agent any
  options { timestamps() }

  parameters {
    choice(name: 'SERVICE', choices: ['auth-service','config-server','vault'], description: 'Which service to build & deploy')
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
        sh '''
          set -eux
          if [ "${SERVICE}" != "vault" ]; then
            chmod +x ./gradlew || true
            ./gradlew :${SERVICE}:clean :${SERVICE}:test :${SERVICE}:bootJar --no-daemon
          else
            echo "Skipping Gradle for vault"
          fi
        '''
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
          sh '"$OC_BIN" project "$NAMESPACE"'
        }
      }
    }

    stage('Apply Manifests') {
      steps {
        sh '''
          set -eux
          if [ "${SERVICE}" = "vault" ]; then
            "$OC_BIN" apply -f k8s/openshift/vault/configmap.yaml   -n "$NAMESPACE"
            "$OC_BIN" apply -f k8s/openshift/vault/statefulset.yaml -n "$NAMESPACE"
          else
            "$OC_BIN" apply -f k8s/openshift/${SERVICE}/buildconfig.yaml -n "$NAMESPACE"
            "$OC_BIN" apply -f k8s/openshift/${SERVICE}/deployment.yaml  -n "$NAMESPACE"
          fi
        '''
      }
    }

    stage('Build Image in OpenShift') {
      when { expression { return params.SERVICE != 'vault' } }
      steps {
        sh '''
          set -eux
          JAR_FILE=$(ls ${SERVICE}/build/libs/*.jar | head -n 1)

          rm -rf build-upload
          mkdir build-upload
          cp "$JAR_FILE" build-upload/app.jar
          cp Dockerfile build-upload/Dockerfile

          BUILD_NAME=$("$OC_BIN" start-build "${SERVICE}" -n "$NAMESPACE" --from-dir=build-upload -o name | sed 's#.*/##')
          echo "Started build: $BUILD_NAME"

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
        sh '''
          set -eux
          if [ "${SERVICE}" = "vault" ]; then
            # StatefulSet rollout (no restart needed)
            "$OC_BIN" rollout status statefulset/vault -n "$NAMESPACE" --timeout=10m || true
            "$OC_BIN" get pods -l app=vault -n "$NAMESPACE" -o wide
          else
            "$OC_BIN" rollout restart deployment/${SERVICE} -n "$NAMESPACE" || true
            "$OC_BIN" rollout status  deployment/${SERVICE} -n "$NAMESPACE" --timeout=5m
          fi
        '''
      }
    }
  }

  post {
    always { archiveArtifacts artifacts: "${SERVICE}/build/libs/*.jar", allowEmptyArchive: true }
  }
}