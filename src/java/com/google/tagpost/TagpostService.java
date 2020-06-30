package com.google.tagpost;

import com.google.tagpost.spanner.DataService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.grpc.StatusRuntimeException;

import java.util.List;
import com.google.inject.Inject;
import com.google.common.flogger.FluentLogger;

/** Encapsulate all RPC methods of {@link TagpostServer} */
public final class TagpostService extends TagpostServiceGrpc.TagpostServiceImplBase {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private final DataService dataService;

  @Inject
  public TagpostService(DataService dataService) {
    this.dataService = dataService;
  }

  @Override
  public void fetchThreadsByTag(
      FetchThreadsByTagRequest req, StreamObserver<FetchThreadsByTagResponse> responseObserver) {
    try {
      FetchThreadsByTagResponse response = fetchThreadsByTagImpl(req);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      Status status = Status.INTERNAL.withDescription(e.getMessage());
      logger.atWarning().withCause(e).log("Fetch Threads By Tag Failed");
      responseObserver.onError(status.asRuntimeException());
    }
  }

  @Override
  public void addThreadWithTag(
      AddThreadWithTagRequest req, StreamObserver<AddThreadWithTagResponse> responseObserver) {
    try {
      AddThreadWithTagResponse response = addThreadWithTagImpl(req);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      Status status = Status.INTERNAL.withDescription(e.getMessage());
      logger.atWarning().withCause(e).log("Add new Thread with PrimaryTag Failed");
      responseObserver.onError(status.asRuntimeException());
    }
  }

  @Override
  public void fetchCommentsUnderThread(
      FetchCommentsUnderThreadRequest req,
      StreamObserver<FetchCommentsUnderThreadResponse> responseObserver) {
    try {
      FetchCommentsUnderThreadResponse response = fetchCommentsUnderThreadImpl(req);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      Status status = Status.INTERNAL.withDescription(e.getMessage());
      logger.atWarning().withCause(e).log("Fetch Comments Under Thread Failed");
      responseObserver.onError(status.asRuntimeException());
    }
  }

  @Override
  public void addCommentUnderThread(
      AddCommentUnderThreadRequest req,
      StreamObserver<AddCommentUnderThreadResponse> responseObserver) {
    try {
      AddCommentUnderThreadResponse response = addCommentUnderThreadImpl(req);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      Status status = Status.INTERNAL.withDescription(e.getMessage());
      logger.atWarning().withCause(e).log("Add new Comment under Thread Failed");
      responseObserver.onError(status.asRuntimeException());
    }
  }

  private FetchThreadsByTagResponse fetchThreadsByTagImpl(FetchThreadsByTagRequest req) {
    String tag = req.getTag();
    logger.atInfo().log("Fetching all Threads with primaryTag = " + tag);
    List<Thread> threadList = dataService.getAllThreadsByTag(tag);
    FetchThreadsByTagResponse response =
        FetchThreadsByTagResponse.newBuilder().addAllThreads(threadList).build();
    return response;
  }

  private AddThreadWithTagResponse addThreadWithTagImpl(AddThreadWithTagRequest req)
      throws Exception {
    logger.atInfo().log(
        "Adding a new thread with primary tag = " + req.getThread().getPrimaryTag().getTagName());
    Thread addedThread = dataService.addNewThreadWithTag(req.getThread());
    AddThreadWithTagResponse response =
        AddThreadWithTagResponse.newBuilder().setThread(addedThread).build();
    return response;
  }

  private FetchCommentsUnderThreadResponse fetchCommentsUnderThreadImpl(
      FetchCommentsUnderThreadRequest req) {
    String threadId = req.getThreadId();
    logger.atInfo().log("Fetching all comments under threadID = " + threadId);
    List<Comment> commentList = dataService.getAllCommentsByThreadId(threadId);
    FetchCommentsUnderThreadResponse response =
        FetchCommentsUnderThreadResponse.newBuilder().addAllComment(commentList).build();
    return response;
  }

  private AddCommentUnderThreadResponse addCommentUnderThreadImpl(AddCommentUnderThreadRequest req)
      throws Exception {
    logger.atInfo().log(
        "Adding a new comment under thread with ThreadID = " + req.getComment().getThreadId());
    Comment addedComment = dataService.addNewCommentUnderThread(req.getComment());
    AddCommentUnderThreadResponse response =
        AddCommentUnderThreadResponse.newBuilder().setComment(addedComment).build();
    return response;
  }
}
