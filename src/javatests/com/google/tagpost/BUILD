load("@rules_java//java:defs.bzl", "java_test")
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

java_test(
    name = "tagpost_client_test",
    srcs = ["TagpostClientTest.java"],
    test_class = "com.google.tagpost.TagpostClientTest",
    runtime_deps = [
        "@maven//:com_google_flogger_flogger_system_backend",
    ],
    deps = [
        "//:tagpost_client",
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
        "@io_grpc_grpc_java//api",
        "@io_grpc_grpc_java//core:inprocess",
        "@io_grpc_grpc_java//stub",
        "@io_grpc_grpc_java//testing",
        "@maven//:org_mockito_mockito_core",
    ],
)

java_test(
    name = "tagpost_server_test",
    srcs = ["TagpostServerTest.java"],
    test_class = "com.google.tagpost.TagpostServerTest",
    deps = [
        "//src/java/com/google/tagpost:tagpost_service",
        "//src/java/com/google/tagpost/spanner:spanner_service",
        "//src/proto:tagpost_java_grpc",
        "//src/proto:tagpost_java_proto",
        "@io_grpc_grpc_java//api",
        "@io_grpc_grpc_java//core:inprocess",
        "@io_grpc_grpc_java//stub",
        "@io_grpc_grpc_java//testing",
        "@maven//:org_mockito_mockito_core",
    ],
)
