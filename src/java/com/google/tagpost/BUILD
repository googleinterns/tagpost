package(default_visibility = ["//visibility:public"])

load("@rules_proto//proto:defs.bzl", "proto_library")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")


java_library(
    name = "tagpost_service",
    srcs = [
        "TagpostService.java",
    ],
    deps = [
        "@io_grpc_grpc_java//stub",
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
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
    ],
    deps = [
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
        "@io_grpc_grpc_java//api",
    ],
)

java_binary(
    name = "tagpost_server",
    srcs = [
        "TagpostServer.java",
    ],
    main_class = "com.google.tagpost.TagpostServer",
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
    ],
    deps = [
        "//src/java/com/google/tagpost:tagpost_service",
        "@io_grpc_grpc_java//api",
    ],
)
