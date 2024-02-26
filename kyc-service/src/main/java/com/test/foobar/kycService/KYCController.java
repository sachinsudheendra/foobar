package com.test.foobar.kycService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.test.foobar.kycService.KYCProto.ProcessKYCRequest;
import static com.test.foobar.kycService.KYCProto.ProcessKYCResponse;

@RestController
public class KYCController {
    @PostMapping(value = "/processKYC", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> processKYC(@RequestBody byte[] payload) throws IOException {
        ProcessKYCRequest request = ProcessKYCRequest.parseFrom(payload);
        ProcessKYCResponse.Builder responseBuilder = ProcessKYCResponse.newBuilder();
        if (request.getMobileNumber().startsWith("9")) {
            responseBuilder.setMessage("KYC success").setSuccess(true);

        } else {
            responseBuilder.setMessage("KYC fail").setSuccess(false);
        }
        return ResponseEntity.ok().body(responseBuilder.build().toByteArray());
    }
}
