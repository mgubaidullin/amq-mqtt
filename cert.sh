#!/bin/bash

# Get cluster apps domain
appsurl=$(oc get ingresses.config.openshift.io cluster  -o template --template '{{.spec.domain}}')
brokerurl="amq-mqtt-mqtt-0-svc-rte-amq-mqtt.$appsurl"
echo "Broker URL: $brokerurl"

keytool -genkey -alias broker -keyalg RSA -keystore broker.ks -storepass $1 -dname CN=$brokerurl
keytool -export -alias broker -keystore broker.ks -file broker_cert -storepass $1 
keytool -genkey -alias client -keyalg RSA -keystore client.ks -storepass $1 -dname CN=localhost
keytool -import -alias broker -keystore client.ts -file broker_cert -storepass $1 -noprompt
oc create secret generic amq-mqtt-mqtt-secret \
    --from-file=broker.ks=broker.ks \
    --from-file=client.ts=client.ts \
    --from-literal=keyStorePassword=$1 \
    --from-literal=trustStorePassword=$1 \
    -n amq-mqtt \
    --dry-run=client \
    -o yaml \
    > broker/instance/amq-mqtt-mqtt-secret.yaml

# oc secrets link sa/amq-broker-operator secret/amq-mqtt-mqtt-secret -n amq-mqtt
