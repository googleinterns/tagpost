package com.google.tagpost;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import com.google.common.flogger.FluentLogger;

/** A client that send requests to TagpostServer */
public class TagpostClient {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final TagpostServiceGrpc.TagpostServiceBlockingStub blockingStub;

  /** Construct client for accessing Tagpost server using the existing channel. */
  public TagpostClient(Channel channel) {
    blockingStub = TagpostServiceGrpc.newBlockingStub(channel);
  }

  public static void main(String[] args) throws Exception {
    // Access a service running on the local machine on port 50053
    String target = "localhost:50053";

    // Create a communication channel to the server.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

    try {
      TagpostClient client = new TagpostClient(channel);
      client.requestAddNewThread("noise");
      client.requestFetchThreads("noise");
      client.requestAddNewComment("12bd2b80-5f47-4367-b0b3-ec0a00ddb271");
      client.requestFetchComments("12bd2b80-5f47-4367-b0b3-ec0a00ddb271");
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  /** Send a request to TagpostServer to fetch all threads by given primary tag */
  public void requestFetchThreads(String primaryTag) {
    logger.atInfo().log(
        "Client will try to send request to fetch all threads with PrimaryTag = " + primaryTag);

    FetchThreadsByTagRequest request =
        FetchThreadsByTagRequest.newBuilder().setTag(primaryTag).build();
    FetchThreadsByTagResponse response;

    try {
      response = blockingStub.fetchThreadsByTag(request);
    } catch (StatusRuntimeException e) {
      logger.atWarning().log("RPC failed: {0}", e.getMessage());
      return;
    }

    logger.atInfo().log("Successfully fetched " + response.getThreadsList().size() + " thread(s)");
  }

  /** Send a request to TagpostServer to add a new thread by given primary tag */
  public void requestAddNewThread(String primaryTag) {
    logger.atInfo().log(
        "Client will try to send request to add a new thread with PrimaryTag = " + primaryTag);

    Thread thread =
        Thread.newBuilder().setPrimaryTag(Tag.newBuilder().setTagName(primaryTag).build()).build();

    AddThreadWithTagRequest request =
        AddThreadWithTagRequest.newBuilder().setThread(thread).build();
    AddThreadWithTagResponse response;

    try {
      response = blockingStub.addThreadWithTag(request);
    } catch (StatusRuntimeException e) {
      logger.atWarning().log("RPC failed: {0}", e.getMessage());
      return;
    }

    logger.atInfo().log("Successfully added a new thread " + response.getThread().toString());
  }

  /** Send a request to TagpostServer to fetch all comment under a specified ThreadID */
  public void requestFetchComments(String threadId) {
    logger.atInfo().log(
        "Client will try to send request to fetch all comments under thread with threadId = "
            + threadId);

    FetchCommentsUnderThreadRequest request =
        FetchCommentsUnderThreadRequest.newBuilder().setThreadId(threadId).build();
    FetchCommentsUnderThreadResponse response;

    try {
      response = blockingStub.fetchCommentsUnderThread(request);
    } catch (StatusRuntimeException e) {
      logger.atWarning().log("RPC failed: {0}", e.getMessage());
      return;
    }

    logger.atInfo().log("Successfully fetched " + response.getCommentList().size() + " comment(s)");
  }

  /** Send a request to TagpostServer to add a new comment under a specified ThreadID */
  public void requestAddNewComment(String threadId) {
    logger.atInfo().log(
        "Client will try to send request to add a new comment under thread with threadId = "
            + threadId);

    Comment comment =
        Comment.newBuilder()
            .setUsername("client")
            .setPrimaryTag(Tag.newBuilder().setTagName("noise").build())
            .addExtraTags(Tag.newBuilder().setTagName("test").build())
            .setCommentContent("new comment")
            .setThreadId(threadId)
            .build();

    AddCommentUnderThreadRequest request =
        AddCommentUnderThreadRequest.newBuilder().setComment(comment).build();
    AddCommentUnderThreadResponse response;

    try {
      response = blockingStub.addCommentUnderThread(request);
    } catch (StatusRuntimeException e) {
      logger.atWarning().log("RPC failed: {0}", e.getMessage());
      return;
    }

    logger.atInfo().log("Successfully added a new comment " + response.getComment().toString());
  }
}
