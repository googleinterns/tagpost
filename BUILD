load("@rules_proto//proto:defs.bzl", "proto_library")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

proto_library(
    name = "tagpost_proto",
    srcs = [
        "src/proto/tagpost.proto"
    ],
)

java_proto_library(
    name = "tagpost_java_proto",
    deps = [
        ":tagpost_proto",
    ],
)

java_grpc_library(
    name = "tagpost_java_grpc",
    srcs = [
        ":tagpost_proto",
    ],
    deps = [
        ":tagpost_java_proto",
    ],
)

java_binary(
    name = "tagpost-server",
    srcs = [
        "src/java/com/google/tagpost/TagPostServer.java",
    ],
    main_class = "com.google.tagpost.TagPostServer",
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
    ],
    deps = [
        "@io_grpc_grpc_java//stub",
        "@io_grpc_grpc_java//api",
        ":tagpost_java_grpc",
        ":tagpost_java_proto",
    ],
)