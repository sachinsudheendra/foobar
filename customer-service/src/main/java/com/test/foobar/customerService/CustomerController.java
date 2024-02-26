package com.test.foobar.customerService;

import okhttp3.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CustomerController {
    private static final String kycEndpoint = "http://kycService:8080/processKYC";
    private static final OkHttpClient client = new OkHttpClient();

    @GetMapping(value = "/performKYC", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> performKYC(@RequestParam String mobileNumber) throws IOException {
        String json = """
                {
                    "mobileNumber": "%s"
                }
                """.formatted(mobileNumber);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(kycEndpoint)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                return ResponseEntity.ok().body(responseBody);
            }
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
}
