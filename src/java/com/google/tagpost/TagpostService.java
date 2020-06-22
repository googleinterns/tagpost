package com.google.tagpost;

import com.google.tagpost.spanner.SpannerService;
import io.grpc.stub.StreamObserver;
import java.util.List;

/** Encapsulate all RPC methods of {@link TagpostServer} */
public final class TagpostService extends TagpostServiceGrpc.TagpostServiceImplBase {

  @Override
  public void fetchMessage(
      FetchMessageRequest req, StreamObserver<FetchMessageResponse> responseObserver) {
    FetchMessageResponse response = fetchMessageImpl(req);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void fetchThreadsByTag(
      FetchThreadsByTagRequest req, StreamObserver<FetchThreadsByTagResponse> responseObserver) {
    FetchThreadsByTagResponse response = fetchThreadsByTagImpl(req);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void fetchCommentsUnderThread(
      FetchCommentsUnderThreadRequest req,
      StreamObserver<FetchCommentsUnderThreadResponse> responseObserver) {
    FetchCommentsUnderThreadResponse response = fetchCommentsUnderThreadImpl(req);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private static FetchThreadsByTagResponse fetchThreadsByTagImpl(FetchThreadsByTagRequest req) {
    Tag tag = req.getTag();
    System.out.println("Fetching all Threads with primaryTag = " + tag.getTagName());
    SpannerService spannerService = SpannerService.create();
    List<Thread> threadList = spannerService.getAllThreadsByTag(tag);
    FetchThreadsByTagResponse response =
        FetchThreadsByTagResponse.newBuilder().addAllThreads(threadList).build();
    return response;
  }

  private static FetchCommentsUnderThreadResponse fetchCommentsUnderThreadImpl(
      FetchCommentsUnderThreadRequest req) {
    long threadId = req.getThreadId();
    System.out.println("Fetching all comments under threadID = " + threadId);
    SpannerService spannerService = SpannerService.create();
    List<Comment> commentList = spannerService.getAllCommentsByThreadId(threadId);
    FetchCommentsUnderThreadResponse response =
        FetchCommentsUnderThreadResponse.newBuilder().addAllComment(commentList).build();
    return response;
  }

  private static FetchMessageResponse fetchMessageImpl(FetchMessageRequest req) {
    FetchMessageResponse response =
        FetchMessageResponse.newBuilder().setMessage("Request received.").build();
    return response;
  }
}
