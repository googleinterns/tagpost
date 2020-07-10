#!/bin/bash

# Builds protos as TypeScript and copies them to UI directory.

set -e
set -u

cd "$(dirname "$0")"

destination_dir="ui/src/compiled_proto"

bazel build //src/proto:tagpost_jspb_proto //src/proto:tagpost_grpc_web_proto
rm -rf "$destination_dir"
mkdir "$destination_dir"
cp -r -t "$destination_dir" \
    bazel-bin/src/proto/tagpost_jspb_proto/src \
    bazel-bin/src/proto/tagpost_grpc_web_proto/src
