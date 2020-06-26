package com.google.tagpost;

import com.google.tagpost.spanner.SpannerService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

/** Unit tests for {@link TagpostServer} */
@RunWith(JUnit4.class)
public class TagpostServerTest {

  /** Automatic graceful shutdown registered servers and channels at the end of test. */
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Mock SpannerService spannerServiceMock;

  private ManagedChannel inProcessChannel;
  private TagpostService tagpostService;
  private Server server;

  @Before
  public void setUp() throws Exception {

    MockitoAnnotations.initMocks(this);
    tagpostService = new TagpostService(spannerServiceMock);

    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    server =
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(tagpostService)
            .build();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(server.start());
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

    // Stub behaviour - return a list of 2 threads
    when(spannerServiceMock.getAllThreadsByTag(Mockito.anyString()))
        .thenReturn(ImmutableList.of(Thread.newBuilder().build(), Thread.newBuilder().build()));

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);

    FetchThreadsByTagResponse reply =
        blockingStub.fetchThreadsByTag(FetchThreadsByTagRequest.newBuilder().build());

    assertEquals(2, reply.getThreadsList().size());
  }

  @Test
  public void fetchCommentsUnderThread_success() throws Exception {

    // Stub behaviour - return a list of 2 comments
    when(spannerServiceMock.getAllCommentsByThreadId(Mockito.anyLong()))
        .thenReturn(ImmutableList.of(Comment.getDefaultInstance(), Comment.getDefaultInstance()));

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);

    FetchCommentsUnderThreadRequest req =
        FetchCommentsUnderThreadRequest.newBuilder().setThreadId(0).build();

    FetchCommentsUnderThreadResponse reply = blockingStub.fetchCommentsUnderThread(req);

    assertEquals(2, reply.getCommentList().size());
  }
}
