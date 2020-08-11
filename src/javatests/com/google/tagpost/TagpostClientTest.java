package com.google.tagpost;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

/** Unit tests for {@link TagpostClient} */
@RunWith(JUnit4.class)
public class TagpostClientTest {

  static final String TAG = "tag";
  static final String THREAD_ID = "000";

  // Automatic graceful shutdown registered servers and channels at the end of test.
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  // Implement a server with fake services
  private final TagpostServiceGrpc.TagpostServiceImplBase serviceImpl =
      mock(
          TagpostServiceGrpc.TagpostServiceImplBase.class,
          delegatesTo(
              new TagpostServiceGrpc.TagpostServiceImplBase() {
                @Override
                public void fetchThreadsByTag(
                    FetchThreadsByTagRequest request,
                    StreamObserver<FetchThreadsByTagResponse> respObserver) {
                  respObserver.onNext(FetchThreadsByTagResponse.getDefaultInstance());
                  respObserver.onCompleted();
                }

                @Override
                public void addThreadWithTag(
                    AddThreadWithTagRequest req,
                    StreamObserver<AddThreadWithTagResponse> responseObserver) {
                  responseObserver.onNext(AddThreadWithTagResponse.getDefaultInstance());
                  responseObserver.onCompleted();
                }

                @Override
                public void fetchCommentsUnderThread(
                    FetchCommentsUnderThreadRequest req,
                    StreamObserver<FetchCommentsUnderThreadResponse> responseObserver) {
                  responseObserver.onNext(FetchCommentsUnderThreadResponse.getDefaultInstance());
                  responseObserver.onCompleted();
                }

                @Override
                public void addCommentUnderThread(
                    AddCommentUnderThreadRequest req,
                    StreamObserver<AddCommentUnderThreadResponse> responseObserver) {
                  responseObserver.onNext(AddCommentUnderThreadResponse.getDefaultInstance());
                  responseObserver.onCompleted();
                }

                @Override
                public void getTagStats(
                    GetTagStatsRequest req, StreamObserver<GetTagStatsResponse> responseObserver) {
                  responseObserver.onNext(GetTagStatsResponse.getDefaultInstance());
                  responseObserver.onCompleted();
                }
              }));

  private TagpostClient client;

  @Before
  public void setUp() throws Exception {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(serviceImpl)
            .build()
            .start());

    // Create a client using the in-process channel;
    client =
        new TagpostClient(
            grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));
  }

  @Test
  public void requestFetchThreads_success() {

    // Call from the client against the fake server
    ArgumentCaptor<FetchThreadsByTagRequest> requestCaptor =
        ArgumentCaptor.forClass(FetchThreadsByTagRequest.class);
    client.requestFetchThreads(TAG);

    // Verify behaviors or state changes from the server side.
    verify(serviceImpl)
        .fetchThreadsByTag(
            requestCaptor.capture(),
            ArgumentMatchers.<StreamObserver<FetchThreadsByTagResponse>>any());
    assertEquals(TAG, requestCaptor.getValue().getTag());
  }

  @Test
  public void requestAddNewThread_success() {

    ArgumentCaptor<AddThreadWithTagRequest> requestCaptor =
        ArgumentCaptor.forClass(AddThreadWithTagRequest.class);
    client.requestAddNewThread(THREAD_ID);

    verify(serviceImpl)
        .addThreadWithTag(
            requestCaptor.capture(),
            ArgumentMatchers.<StreamObserver<AddThreadWithTagResponse>>any());
    assertEquals(THREAD_ID, requestCaptor.getValue().getThread().getPrimaryTag().getTagName());
  }

  @Test
  public void requestFetchComments_success() {

    ArgumentCaptor<FetchCommentsUnderThreadRequest> requestCaptor =
        ArgumentCaptor.forClass(FetchCommentsUnderThreadRequest.class);
    client.requestFetchComments(THREAD_ID);

    verify(serviceImpl)
        .fetchCommentsUnderThread(
            requestCaptor.capture(),
            ArgumentMatchers.<StreamObserver<FetchCommentsUnderThreadResponse>>any());
    assertEquals(THREAD_ID, requestCaptor.getValue().getThreadId());
  }

  @Test
  public void requestAddNewComment_success() {

    ArgumentCaptor<AddCommentUnderThreadRequest> requestCaptor =
        ArgumentCaptor.forClass(AddCommentUnderThreadRequest.class);
    client.requestAddNewComment(THREAD_ID);

    verify(serviceImpl)
        .addCommentUnderThread(
            requestCaptor.capture(),
            ArgumentMatchers.<StreamObserver<AddCommentUnderThreadResponse>>any());
    assertEquals(THREAD_ID, requestCaptor.getValue().getComment().getThreadId());
  }

    @Test
    public void requestGetTagStats_success() {

        ArgumentCaptor<GetTagStatsRequest> requestCaptor =
                ArgumentCaptor.forClass(GetTagStatsRequest.class);
        client.requestGetTagStats(TAG);

        verify(serviceImpl)
                .getTagStats(
                        requestCaptor.capture(),
                        ArgumentMatchers.<StreamObserver<GetTagStatsResponse>>any());
        assertEquals(TAG, requestCaptor.getValue().getTag());
    }
}
