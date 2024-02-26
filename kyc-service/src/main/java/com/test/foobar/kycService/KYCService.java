package com.test.foobar.kycService;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import static com.test.foobar.kycService.KYCProto.ProcessKYCRequest;
import static com.test.foobar.kycService.KYCProto.ProcessKYCResponse;

@Service
public class KYCService extends KYCServiceGrpc.KYCServiceImplBase {
    @Override
    public void process(ProcessKYCRequest request, StreamObserver<ProcessKYCResponse> responseObserver) {
        ProcessKYCResponse.Builder responseBuilder = ProcessKYCResponse.newBuilder();
        if (request.getMobileNumber().startsWith("9")) {
            responseBuilder.setMessage("KYC success").setSuccess(true);

        } else {
            responseBuilder.setMessage("KYC fail").setSuccess(false);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
