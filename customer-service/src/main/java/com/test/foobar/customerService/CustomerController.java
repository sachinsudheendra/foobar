package com.test.foobar.customerService;

import com.google.protobuf.util.JsonFormat;
import com.test.foobar.kycService.KYCProto;
import okhttp3.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CustomerController {
    private static final String kycEndpoint = "http://kycservice:8080/processKYC";
    private static final OkHttpClient client = new OkHttpClient();

    @GetMapping(value = "/performKYC", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> performKYC(@RequestParam String mobileNumber) throws IOException {
        KYCProto.ProcessKYCRequest request = KYCProto.ProcessKYCRequest.newBuilder()
                .setMobileNumber(mobileNumber)
                .build();
        RequestBody body = RequestBody.create(request.toByteArray(), MediaType.parse("application/octet-stream"));
        Request httpRequest = new Request.Builder().url(kycEndpoint).post(body).build();
        try (Response res = client.newCall(httpRequest).execute()) {
            KYCProto.ProcessKYCResponse responseBody = KYCProto.ProcessKYCResponse.parseFrom(res.body().bytes());
            String jsonResponse = JsonFormat.printer().print(responseBody);
            if (res.isSuccessful()) {
                return ResponseEntity.ok().body(jsonResponse);
            }
            return ResponseEntity.badRequest().body(jsonResponse);
        }
    }
}
