package(default_visibility = ["//visibility:public"])

load("@io_bazel_rules_docker//container:container.bzl", "container_image")

alias(
    name = "tagpost_server",
    actual = "//src/java/com/google/tagpost:tagpost_server.binary",
)

alias(
    name = "tagpost_client",
    actual = "//src/java/com/google/tagpost:tagpost_client",
)

container_image(
    name = "tagpost_server_dev",
    base = "//src/java/com/google/tagpost:tagpost_server_prod",
    env = {
        "GOOGLE_APPLICATION_CREDENTIALS": "/credentials/tagpost-local-dev-key.json",
    },
    volumes = ["/credentials"],
)
