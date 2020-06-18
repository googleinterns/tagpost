package com.google.tagpost;

import static org.junit.Assert.assertEquals;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/** Unit tests for {@link TagpostServer} */
@RunWith(JUnit4.class)
public class TagpostServerTest {
  /** Automatic graceful shutdown registered servers and channels at the end of test. */
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  /**
   * To test the server, make calls with a real stub using the in-process channel, and verify
   * behaviors or state changes from the client side.
   */
  @Test
  public void replyMessage_success() throws Exception {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(new TagpostService())
            .build()
            .start());

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
            TagpostServiceGrpc.newBlockingStub(
            // Create a client channel and register for automatic graceful shutdown.
            grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    String request_id = "222";
    FetchMessageResponse reply =
        blockingStub.fetchMessage(FetchMessageRequest.newBuilder().setRequestId(request_id).build());

    assertEquals("Request received.", reply.getMessage());
  }
}
