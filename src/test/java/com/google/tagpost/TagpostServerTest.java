package com.google.tagpost;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** Unit tests for {@link TagpostServer} */
@RunWith(JUnit4.class)
public class TagpostServerTest {
  /** Automatic graceful shutdown registered servers and channels at the end of test. */
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private ManagedChannel inProcessChannel;

  @Before
  public void setUp() throws Exception {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(new TagpostService())
            .build()
            .start());
    inProcessChannel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
  }

  /**
   * To test the server, make calls with a real stub using the in-process channel, and verify
   * behaviors or state changes from the client side.
   */
  @Test
  public void replyMessage_success() throws Exception {

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);
    String request_id = "222";
    FetchMessageResponse reply =
        blockingStub.fetchMessage(
            FetchMessageRequest.newBuilder().setRequestId(request_id).build());

    assertEquals("Request received.", reply.getMessage());
  }

  @Test
  public void fetchThreadsByTag_success() throws Exception {

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);

    FetchThreadsByTagResponse reply =
        blockingStub.fetchThreadsByTag(
            FetchThreadsByTagRequest.newBuilder()
                .setTag(Tag.newBuilder().setTagName("noise").build())
                .build());

    List<Thread> expectedThreadList = new ArrayList<>();
    expectedThreadList.add(
        Thread.newBuilder()
            .setThreadId(0)
            .setPrimaryTag(Tag.newBuilder().setTagName("noise"))
            .build());
    expectedThreadList.add(
        Thread.newBuilder()
            .setThreadId(1)
            .setPrimaryTag(Tag.newBuilder().setTagName("noise"))
            .build());

    assertEquals(expectedThreadList, reply.getThreadsList());
  }

  @Test
  public void fetchCommentsUnderThread_success() throws Exception {

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);

    FetchCommentsUnderThreadRequest req0 =
        FetchCommentsUnderThreadRequest.newBuilder().setThreadId(0).build();
    FetchCommentsUnderThreadRequest req1 =
        FetchCommentsUnderThreadRequest.newBuilder().setThreadId(1).build();

    FetchCommentsUnderThreadResponse reply0 = blockingStub.fetchCommentsUnderThread(req0);
    FetchCommentsUnderThreadResponse reply1 = blockingStub.fetchCommentsUnderThread(req1);

    assertEquals(reply0.getCommentList().size(), 2);
    assertEquals(reply1.getCommentList().size(), 1);
  }
}
