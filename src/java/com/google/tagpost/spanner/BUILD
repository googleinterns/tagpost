package(default_visibility = ["//visibility:public"])

java_library(
    name = "data_service",
    srcs = ["DataService.java"],
    deps = ["//src/proto:tagpost_java_proto"],
)

java_library(
    name = "spanner_service",
    srcs = ["SpannerService.java"],
    deps = [
        ":data_service",
        "//src/proto:tagpost_java_proto",
        "@maven//:com_google_cloud_google_cloud_core",
        "@maven//:com_google_cloud_google_cloud_spanner",
        "@maven//:com_google_guava_guava",
    ],
)
