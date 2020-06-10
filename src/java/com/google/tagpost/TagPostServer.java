package com.google.tagpost;

import com.google.tagpost.ResponseGrpc;
import com.google.tagpost.Reply;
import com.google.tagpost.Request;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A dummy server that only manages startup/shutdown.
 */
public class TagPostServer {
  private static final Logger logger = Logger.getLogger(TagPostServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50053;
    server = ServerBuilder.forPort(port)
        .addService(new ResponseImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          TagPostServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final TagPostServer server = new TagPostServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class ResponseImpl extends ResponseGrpc.ResponseImplBase {

    @Override
    public void response(Request req, StreamObserver<Reply> responseObserver) {
      Reply reply = Reply.newBuilder().setMessage("Hello " + req.getRequestId()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
