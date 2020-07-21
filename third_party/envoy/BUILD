load(
    "@io_bazel_rules_docker//container:container.bzl",
    "container_image",
    "container_layer",
)
load("//:common_def.bzl", "TAGPOST_GRPC_PORT", "TAGPOST_GRPC_WEB_PORT")
load("//third_party:decompress.bzl", "unxz")

unxz(
    name = "envoy_tar",
    input = "envoy.tar.xz",
)

container_layer(
    name = "config",
    data_path = "config",
    files = [
        "config/etc/envoy/envoy.template.yaml",
        "config/opt/bootstrap/start_envoy.sh",
    ],
)

container_image(
    name = "envoy_image",
    base = ":envoy_tar",
    cmd = [],
    entrypoint = ["/opt/bootstrap/start_envoy.sh"],
    env = {
        "TAGPOST_GRPC_PORT": TAGPOST_GRPC_PORT,
        "TAGPOST_GRPC_WEB_PORT": TAGPOST_GRPC_WEB_PORT,
        "PATH": "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
    },
    layers = [":config"],
    ports = [
        TAGPOST_GRPC_PORT + "/tcp",
        TAGPOST_GRPC_WEB_PORT + "/tcp",
    ],
    repository = "tagpost_server",
)
