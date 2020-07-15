package(default_visibility = ["//visibility:public"])

load("@io_bazel_rules_docker//java:image.bzl", "java_image")
load("@io_bazel_rules_docker//container:container.bzl", "container_image", "container_push")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")
load("//:common_def.bzl", "TAGPOST_GRPC_PORT")

java_library(
    name = "tagpost_injector_module",
    srcs = ["TagpostModule.java"],
    deps = [
        "//src/java/com/google/tagpost/spanner:data_service",
        "//src/java/com/google/tagpost/spanner:spanner_service",
        "@maven//:com_google_inject_guice",
    ],
)

java_library(
    name = "tagpost_service",
    srcs = [
        "TagpostService.java",
    ],
    runtime_deps = [
        "@maven//:com_google_flogger_flogger_system_backend",
    ],
    deps = [
        "//src/java/com/google/tagpost/spanner:data_service",
        "//src/java/com/google/tagpost/spanner:spanner_service",
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
        "@io_grpc_grpc_java//api",
        "@io_grpc_grpc_java//stub",
        "@maven//:com_google_flogger_flogger",
        "@maven//:com_google_inject_guice",
    ],
)

java_binary(
    name = "tagpost_client",
    srcs = [
        "TagpostClient.java",
    ],
    main_class = "com.google.tagpost.TagpostClient",
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
        "@maven//:com_google_flogger_flogger_system_backend",
    ],
    deps = [
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
        "@io_grpc_grpc_java//api",
        "@maven//:com_google_flogger_flogger",
    ],
)

java_image(
    name = "tagpost_server",
    srcs = [
        "TagpostServer.java",
    ],
    main_class = "com.google.tagpost.TagpostServer",
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
        "@maven//:com_google_flogger_flogger_system_backend",
    ],
    deps = [
        "//src/java/com/google/tagpost:tagpost_injector_module",
        "//src/java/com/google/tagpost:tagpost_service",
        "@io_grpc_grpc_java//api",
        "@maven//:com_google_flogger_flogger",
        "@maven//:com_google_inject_guice",
    ],
)

container_image(
    name = "tagpost_server_prod",
    base = ":tagpost_server",
    env = {
        "TAGPOST_GRPC_PORT": TAGPOST_GRPC_PORT,
    },
    ports = [
        TAGPOST_GRPC_PORT + "/tcp",
    ],
)

container_push(
    name = "tagpost_server_push",
    format = "Docker",
    image = ":tagpost_server",
    registry = "gcr.io",
    repository = "testing-bigtest/tagpost_server",
    tag = "dev",
)
