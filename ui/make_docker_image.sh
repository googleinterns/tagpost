#!/bin/bash

set -e
set -u
set -o pipefail

# Uncomment one of the following lines to select your ng command
ng_command=(ng)
# ng_command=(yarn run ng)
# ng_command=(npm run ng)

image_name="gcr.io/testing-bigtest/tagpost-frontend:dev"

cd "$(dirname "$0")"
if [[ -e dist ]]; then
  rm -r dist
fi
mkdir dist
cp -r nginx_conf dist/nginx_conf
"${ng_command[@]}" build --prod
docker build -f Dockerfile -t "$image_name" dist
