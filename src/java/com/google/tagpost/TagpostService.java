package com.google.tagpost;

import com.google.tagpost.TagpostServiceGrpc;
import com.google.tagpost.SimpleReplyRequest;
import com.google.tagpost.SimpleReplyResponse;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public final class TagpostService extends TagpostServiceGrpc.TagpostServiceImplBase {

    @Override
    public void simpleReply(SimpleReplyRequest req, StreamObserver<SimpleReplyResponse> responseObserver) {
        SimpleReplyResponse response = determineResponse(req);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private static SimpleReplyResponse determineResponse(SimpleReplyRequest req) {
        SimpleReplyResponse response = SimpleReplyResponse.newBuilder().setMessage("Hello " + req.getRequestId()).build();
        return response;
    }
}
