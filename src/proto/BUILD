package(default_visibility = ["//visibility:public"])

load("@rules_proto//proto:defs.bzl", "proto_library")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

proto_library(
    name = "tagpost_proto",
    srcs = [
        "tagpost.proto",
    ],
    deps = [
        "@com_google_protobuf//:timestamp_proto",
    ],
)

proto_library(
    name = "tagpost_rpc_proto",
    srcs = [
        "tagpost_rpc.proto",
    ],
    deps = [
        ":tagpost_proto",
    ],
)

java_proto_library(
    name = "tagpost_java_proto",
    deps = [
        ":tagpost_proto",
        ":tagpost_rpc_proto",
    ],
)

java_grpc_library(
    name = "tagpost_java_grpc",
    srcs = [
        ":tagpost_rpc_proto",
    ],
    deps = [
        ":tagpost_java_proto",
    ],
)
