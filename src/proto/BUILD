package(default_visibility = ["//visibility:public"])

load("@rules_proto//proto:defs.bzl", "proto_library")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

proto_library(
    name = "tagpost_proto",
    srcs = [
        "tagpost.proto",
        "tagpost_rpc.proto",
    ],
    deps = [
        "@com_google_protobuf//:timestamp_proto",
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
