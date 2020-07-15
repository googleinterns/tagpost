#!/bin/bash

set -e
set -u
set -o pipefail

echo "Generating envoy.yaml"
sed 's/<<<TAGPOST_GRPC_PORT>>>/'"$TAGPOST_GRPC_PORT"'/g;s/<<<TAGPOST_GRPC_WEB_PORT>>>/'"$TAGPOST_GRPC_WEB_PORT"'/g' \
    /etc/envoy/envoy.template.yaml \
    > /etc/envoy/envoy.yaml

echo "Starting server"
exec envoy -c /etc/envoy/envoy.yaml
