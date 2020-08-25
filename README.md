<h1 align="center">
  <br>
  <img src="https://user-images.githubusercontent.com/61160875/90850784-b73f1c00-e340-11ea-96f2-4d251593328d.png">
  <br>
</h1>


<p align="center">
  :zap: Make information sharing smooth and straightforward. 
</p>

## Table of Contents :bookmark_tabs:

- [Introduction](#introduction-bulb)
- [Features](#features-point_down)
- [Build Process](#build-process-hammer)
- [Tech Stack](#tech-stack-arrow_lower_right)
- [Demo](#demo-arrow_forward)
- [Contributors](#contributors-tada)
- [Acknowledgments](#acknowledgments)


<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction :bulb:

At the very beginning, TagPost is going to be built as a tool for internal use. As the internship program at Google this year went virtual, we decided to shift this project to be open source. [See this](https://blog.google/inside-google/working-google/google-internships-go-virtual-help-open-source/)<br /> 
<br>
TagPost is a full stack project to support information sharing. It maps a ``tag`` to discussions, comments and statistics(charts). Any keyword can be used as a ``tag`` here, TagPost serves as a forum for ``tag``. Users will be able to use TagPost to search, retrieve and post info using the ``tag``. 

TagPost is built on an end-to-end gRPC service architecture using gRPC-web. [gRPC-web](https://github.com/grpc/grpc-web) is a JavaScript client library that enables our Angular web app to interact directly with backend gRPC server with the help of [Envoy](https://www.envoyproxy.io/). 

<br>
  <img src="https://user-images.githubusercontent.com/61160875/90851368-7a742480-e342-11ea-8c1b-657762acca9d.png">
<br>

## Tech Stack :arrow_lower_right:
Here are some of the amazing tools and librarys we used in TagPost
* [Bazel](https://github.com/bazelbuild/bazel)  A fast, scalable, multi-language and extensible build system
* [gRPC-java](https://github.com/grpc/grpc-java)  The Java gRPC implementation.
* [gRPC-web](https://github.com/grpc/grpc-web)  gRPC for web client
* [Angular](https://angular.io/)  One framework. Mobile & desktop.
* [Bulma](https://bulma.io/) Modern CSS framework based on Flexbox
* [Envoy](https://www.envoyproxy.io/)  envoy is an open source edge and service proxy <a>. 

## Build Process :hammer:
* [Install Bazel (version 3.3.1)](https://docs.bazel.build/install.html) 

* A spanner instance is required to build TagPost in your local. You need to setup [authentication](https://cloud.google.com/spanner/docs/reference/libraries#setting_up_authentication) by creating an service account 

```java
  // SpannerService.java
  // Enter YOUR_INSTANCE_ID, YOUR_DATABASE_ID
  private void initDb() {
    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    db = DatabaseId.of(spannerOptions.getProjectId(),YOUR_INSTANCE_ID, YOUR_DATABASE_ID);
    spanner = spannerOptions.getService();
  }
```
* Build TagPost backend server locally
```bash
  # 1. First, please clone the repo
  git clone https://github.com/googleinterns/tagpost.git
  cd tagpost
  
  # 2. build server 
  bazel build :tagpost_server
  # 3. run server  
  bazel run :tagpost_server 
  # 4. run server unit test 
  bazel test //src/javatests/com/google/tagpost:tagpost_server_test
```

* Build Envoy Image
```bash
  bazel build third_party/envoy:envoy_image
```

* Build TagPost frontend locally
```bash
# 1. run script to generate the proto messages and the service client stub
cd ui
./make_ts_proto.sh

# 2. build docker image
./make_docker_image.sh

```

## Features :point_down: 

Here is a few of the things you can do with TagPost:

For a tag of choice, you can:
* Easily search for discussions
* Start your own discussion 
* View and add comments under any discussion thread
* View data visualizations for the tag.

See demos for more details.

## Demo :arrow_forward: 

:round_pushpin: **click to enlarge img** 
- A list of threads with ``experiment-id-was-deleted`` as the tag 
<br>
  <img src="https://user-images.githubusercontent.com/61160875/90855138-d04dca00-e34c-11ea-96b4-a53458790c8e.png" width="240" height="180">
<br>

- A detailed thread page.
<br>
  <img src="https://user-images.githubusercontent.com/61160875/90855994-ebb9d480-e34e-11ea-9a32-ef42d4ccdb09.png" width="240" height="180">
<br>

- Add new thread.
<br>
  <img src="https://user-images.githubusercontent.com/61160875/90856257-8e725300-e34f-11ea-8a65-b8e3ab2edae3.png" width="240" height="180">
<br>

## Contributors :tada:

This project is brought to you by these <a href="https://github.com/googleinterns/tagpost/graphs/contributors"> contributors </a> 


## Acknowledgments

This project is part of Google 2020 Summer Internship Program.  
**This is not an officially supported Google product**.




