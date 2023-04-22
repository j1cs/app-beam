#!/usr/bin/env sh
set -e

if ! command -v curl &>/dev/null; then
  echo "curl could not be found"
  echo "please install curl on your machine https://curl.se/"
  echo "then run \make $exec\" again"
  exit 1
fi

trap "make down" SIGINT

while [ -z $(curl -s http://localhost:$PUBSUB_PORT) ]; do
  sleep 1
done

export PUBSUB_EMULATOR_PORT=localhost:$PUBSUB_PORT
./mvnw mn:run -DskipTests=true -Dmn.jvmArgs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" -Dmn.appArgs="--dataflow-args=\"--runner=DirectRunner --inputSubscription=${PUBSUB_SUBSCRIPTION} --projectId=${GCP_PROJECT_ID} --outputTopic=projects/${GCP_PROJECT_ID}/topics/${PUBSUB_TOPIC} --pubsubRootUrl=http://localhost:${PUBSUB_PORT}\""
