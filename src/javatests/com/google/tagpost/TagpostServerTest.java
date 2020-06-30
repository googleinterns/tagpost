package com.google.tagpost;

import com.google.tagpost.spanner.SpannerService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Unit tests for {@link TagpostServer} */
@RunWith(JUnit4.class)
public class TagpostServerTest {

  /** Automatic graceful shutdown registered servers and channels at the end of test. */
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Rule public MockitoRule rule = MockitoJUnit.rule();

  @Mock SpannerService spannerServiceMock;

  private ManagedChannel inProcessChannel;
  private TagpostService tagpostService;
  private Server server;

  @Before
  public void setUp() throws Exception {

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
    inProcessChannel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
  }

  /**
   * To test the server, make calls with a real stub using the in-process channel, and verify
   * behaviors or state changes from the client side.
   */
  @Test
  public void fetchThreadsByTag_success() throws Exception {

    ImmutableList expectedList =
        ImmutableList.of(
            Thread.newBuilder()
                .setThreadId("00")
                .setPrimaryTag(Tag.newBuilder().setTagName("tag1"))
                .build(),
            Thread.newBuilder()
                .setThreadId("11")
                .setPrimaryTag(Tag.newBuilder().setTagName("tag2"))
                .build());
    // Stub behaviour - return a list of 2 threads
    when(spannerServiceMock.getAllThreadsByTag(Mockito.anyString())).thenReturn(expectedList);

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);
    FetchThreadsByTagResponse reply =
        blockingStub.fetchThreadsByTag(FetchThreadsByTagRequest.getDefaultInstance());

    assertEquals(expectedList, reply.getThreadsList());
  }

  @Test
  public void addThreadByTag_success() throws Exception {

    Thread expectedThread = Thread.newBuilder().setThreadId("00").build();
    // Stub behaviour - return a thread
    when(spannerServiceMock.addNewThreadWithTag(Mockito.anyString(), Mockito.any()))
        .thenReturn(expectedThread);

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);
    AddThreadWithTagResponse reply =
        blockingStub.addThreadWithTag(AddThreadWithTagRequest.getDefaultInstance());

    assertEquals(expectedThread, reply.getThread());
  }

  @Test
  public void fetchCommentsUnderThread_success() throws Exception {

    ImmutableList expectedList =
        ImmutableList.of(
            Comment.newBuilder()
                .setThreadId("0")
                .setUsername("user1")
                .setCommentContent("random")
                .setCommentId("01")
                .build(),
            Comment.newBuilder()
                .setThreadId("0")
                .setUsername("user2")
                .setCommentContent("random")
                .setCommentId("02")
                .build());
    // Stub behaviour - return a list of 2 comments
    when(spannerServiceMock.getAllCommentsByThreadId(Mockito.anyString())).thenReturn(expectedList);

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);
    FetchCommentsUnderThreadResponse reply =
        blockingStub.fetchCommentsUnderThread(FetchCommentsUnderThreadRequest.getDefaultInstance());

    assertEquals(expectedList, reply.getCommentList());
  }

  @Test
  public void addCommentUnderThread_success() throws Exception {

    Comment expectedComment =
        Comment.newBuilder()
            .setThreadId("0")
            .setUsername("user2")
            .setCommentContent("random")
            .setCommentId("02")
            .build();
    // Stub behaviour - return a comment
    when(spannerServiceMock.addNewCommentUnderThread(Mockito.any())).thenReturn(expectedComment);

    TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub =
        TagpostServiceGrpc.newBlockingStub(inProcessChannel);
    AddCommentUnderThreadResponse reply =
        blockingStub.addCommentUnderThread(AddCommentUnderThreadRequest.getDefaultInstance());

    assertEquals(expectedComment, reply.getComment());
  }
}
