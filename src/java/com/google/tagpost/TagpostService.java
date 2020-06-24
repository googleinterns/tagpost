package com.google.tagpost;

import com.google.tagpost.spanner.DataService;
import io.grpc.stub.StreamObserver;
import java.util.List;
import com.google.inject.Inject;
import com.google.common.flogger.FluentLogger;

/** Encapsulate all RPC methods of {@link TagpostServer} */
public final class TagpostService extends TagpostServiceGrpc.TagpostServiceImplBase {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private DataService dataService;

  @Inject
  public TagpostService(DataService dataService) {
    this.dataService = dataService;
  }

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

  private FetchThreadsByTagResponse fetchThreadsByTagImpl(FetchThreadsByTagRequest req) {
    Tag tag = req.getTag();
    logger.atInfo().log("Fetching all Threads with primaryTag = " + tag.getTagName());
    List<Thread> threadList = dataService.getAllThreadsByTag(tag);
    FetchThreadsByTagResponse response =
        FetchThreadsByTagResponse.newBuilder().addAllThreads(threadList).build();
    return response;
  }

  private FetchCommentsUnderThreadResponse fetchCommentsUnderThreadImpl(
          FetchCommentsUnderThreadRequest req) {
    long threadId = req.getThreadId();
    logger.atInfo().log("Fetching all comments under threadID = " + threadId);
    List<Comment> commentList = dataService.getAllCommentsByThreadId(threadId);
    FetchCommentsUnderThreadResponse response =
        FetchCommentsUnderThreadResponse.newBuilder().addAllComment(commentList).build();
    return response;
  }

  private FetchMessageResponse fetchMessageImpl(FetchMessageRequest req) {
    FetchMessageResponse response =
        FetchMessageResponse.newBuilder().setMessage("Request received.").build();
    return response;
  }
}
