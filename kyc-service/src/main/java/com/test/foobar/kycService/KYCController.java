package com.test.foobar.kycService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class KYCController {
    @PostMapping(value = "/processKYC", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processKYC(@RequestBody Map<String, String> payload) throws Exception {
        Thread.sleep(200);
        if (payload.containsKey("mobileNumber")) {
            String mobileNumber = payload.get("mobileNumber");
            if (mobileNumber.startsWith("9")) {
                var response = new HashMap<String, Object>() {{
                    put("message", "KYC success");
                    put("success", true);
                }};
                return ResponseEntity.ok(response);
            } else {
                var response = new HashMap<String, Object>() {{
                    put("message", "KYC fail");
                    put("success", false);
                }};
                return ResponseEntity.badRequest().body(response);
            }
        }
        var response = new HashMap<String, Object>() {{
            put("message", "mobile number not present");
            put("success", false);
        }};
        return ResponseEntity.badRequest().body(response);
    }
}
