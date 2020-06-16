package com.google.tagpost;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.ManagedChannel;
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
  /** Automatic graceful shutdown registered servers and channels at the end of test. */
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
  // Implement a fake service
  private final TagpostServiceGrpc.TagpostServiceImplBase serviceImpl =
      mock(
          TagpostServiceGrpc.TagpostServiceImplBase.class,
          delegatesTo(
              new TagpostServiceGrpc.TagpostServiceImplBase() {
                @Override
                public void fetchMessage(
                    FetchMessageRequest request,
                    StreamObserver<FetchMessageResponse> respObserver) {
                  respObserver.onNext(FetchMessageResponse.getDefaultInstance());
                  respObserver.onCompleted();
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

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a HelloWorldClient using the in-process channel;
    client = new TagpostClient(channel);
  }

  /** Call from the client against the fake server */
  @Test
  public void messageDeliveredToServer() {
    ArgumentCaptor<FetchMessageRequest> requestCaptor =
        ArgumentCaptor.forClass(FetchMessageRequest.class);

    String request_id = "111";
    client.communicate(request_id);

    verify(serviceImpl)
        .fetchMessage(
            requestCaptor.capture(), ArgumentMatchers.<StreamObserver<FetchMessageResponse>>any());
    assertEquals(request_id, requestCaptor.getValue().getRequestId());
  }
}
