#!/bin/bash

set -e
set -u
set -o pipefail

sed 's/<<<TAGPOST_GRPC_WEB_PORT>>>/'"$TAGPOST_GRPC_WEB_PORT"'/g;s/<<<TAGPOST_GRPC_WEB_HOST>>>/'"$TAGPOST_GRPC_WEB_HOST"'/g' \
    /opt/bootstrap/default.conf.template \
    > /etc/nginx/conf.d/default.conf
exec nginx -g "daemon off;"
