package com.google.tagpost;

import com.google.common.flogger.FluentLogger;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;

/** A gRPC server that serve the Tagpost service. */
public class TagpostServer {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private Server server;

  /** Main launches the server from the command line. */
  public static void main(String[] args) throws IOException, InterruptedException {
    final TagpostServer server = new TagpostServer();
    server.start();
    server.blockUntilShutdown();
  }

  private void start() throws IOException {
    /* The port on which the server should run */
    String portStr = System.getenv("TAGPOST_GRPC_PORT");
    if (portStr == null) {
      throw new IllegalArgumentException("Environment variable TAGPOST_GRPC_PORT is unset");
    }
    int port = Integer.parseInt(portStr);

    Injector injector = Guice.createInjector(new TagpostModule());
    TagpostService tagpostService = injector.getInstance(TagpostService.class);

    server = ServerBuilder.forPort(port).addService(tagpostService).build().start();
    logger.atInfo().log("Server started, listening on " + port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                logger.atInfo().log("*** shutting down gRPC server since JVM is shutting down");
                try {
                  TagpostServer.this.stop();
                } catch (InterruptedException e) {
                  e.printStackTrace(System.err);
                }
                logger.atInfo().log("*** server shut down");
              }
            });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /** Await termination on the main thread since the grpc library uses daemon threads. */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
