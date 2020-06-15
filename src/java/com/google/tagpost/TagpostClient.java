package com.google.tagpost;

import com.google.tagpost.FetchMessageRequest;
import com.google.tagpost.FetchMessageResponse;
import com.google.tagpost.TagpostServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A dummy client that send a request to TagpostServer */
public class TagpostClient {
  private static final Logger logger = Logger.getLogger(TagpostClient.class.getName());

  private final TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub;

  /** Construct client for accessing Tagpost server using the existing channel. */
  public TagpostClient(Channel channel) {
    blockingStub = TagpostServiceGrpc.newBlockingStub(channel);
  }

  public static void main(String[] args) throws Exception {
    // Access a service running on the local machine on port 50053
    String target = "localhost:50053";
    // Set a default request_id
    String request_id = "000";
    // Allow passing in the target strings as command line arguments
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [target]");
        System.err.println();
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
      target = args[0];
    }

    // Create a communication channel to the server.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    try {
      TagpostClient client = new TagpostClient(channel);
      client.communicate(request_id);
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  /** Send a request to Server and get a response. */
  public void communicate(String request_id) {
    logger.info("Will try to send request to server...");

    FetchMessageRequest request = FetchMessageRequest.newBuilder().setRequestId(request_id).build();
    FetchMessageResponse response;
    try {
      response = blockingStub.fetchMessage(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info(response.getMessage());
  }
}
