package com.google.tagpost;

import com.google.tagpost.FetchMessageRequest;
import com.google.tagpost.FetchMessageResponse;
import com.google.tagpost.TagpostServiceGrpc;
import io.grpc.stub.StreamObserver;

public final class TagpostService extends TagpostServiceGrpc.TagpostServiceImplBase {

    @Override
    public void fetchMessage(FetchMessageRequest req, StreamObserver<FetchMessageResponse> responseObserver) {
        FetchMessageResponse response = determineResponse(req);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private static FetchMessageResponse determineResponse(FetchMessageRequest req) {
        FetchMessageResponse response = FetchMessageResponse.newBuilder().setMessage("Request received. request_id = " + req.getRequestId()).build();
        return response;
    }
}
