package com.test.foobar.customerService;

import com.google.protobuf.util.JsonFormat;
import com.test.foobar.kycService.KYCProto;
import com.test.foobar.kycService.KYCServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import okhttp3.OkHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CustomerController {
    private static final String kycEndpoint = "http://kycservice:8080/processKYC";
    private static final OkHttpClient client = new OkHttpClient();
    private static final KYCServiceGrpc.KYCServiceBlockingStub kycServiceClient;

    static {
        Channel channel = ManagedChannelBuilder.forAddress("kycservice", 9009).build();
        kycServiceClient = KYCServiceGrpc.newBlockingStub(channel);
    }

    @GetMapping(value = "/performKYC", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> performKYC(@RequestParam String mobileNumber) throws IOException {
        KYCProto.ProcessKYCRequest request = KYCProto.ProcessKYCRequest.newBuilder()
                .setMobileNumber(mobileNumber)
                .build();
        KYCProto.ProcessKYCResponse response = kycServiceClient.process(request);
        String jsonResponse = JsonFormat.printer().print(response);
        return ResponseEntity.ok().body(jsonResponse);
    }
}
