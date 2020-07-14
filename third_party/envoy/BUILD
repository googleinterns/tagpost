load(
    "@io_bazel_rules_docker//container:container.bzl",
    "container_image",
    "container_layer",
)
load("//third_party:decompress.bzl", "unxz")

unxz(
    name = "envoy_tar",
    input = "envoy.tar.xz",
)

container_layer(
    name = "config",
    data_path = "config",
    files = [
        "config/etc/envoy/envoy.yaml",
    ],
)

container_image(
    name = "envoy_image",
    base = ":envoy_tar",
    cmd = [
        "envoy",
        "-c",
        "/etc/envoy/envoy.yaml",
    ],
    env = {
        "PATH": "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
    },
    layers = [":config"],
    ports = [
        "8080/tcp",
    ],
    repository = "tagpost_server",
)
