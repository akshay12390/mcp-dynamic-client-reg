package org.springframework.ai.mcp.sample.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CustomAuthMetdataController {

    @Value("${spring.security.oauth2.authorizationserver.issuer-uri:http://localhost:8080}")
    private String issuer;

    @GetMapping("/custom-well-known/oauth-authorization-server")
    public Map<String, Object> customWellKnown() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("issuer", issuer);
        metadata.put("authorization_endpoint", issuer + "/oauth2/authorize");
        metadata.put("token_endpoint", issuer + "/oauth2/token");
        metadata.put("jwks_uri", issuer + "/oauth2/jwks");
        metadata.put("registration_endpoint", issuer + "/connect/register");
        metadata.put("scopes", Arrays.asList("client.create", "client.read", "weather.read"));
        // Add any other fields you want to expose

        return metadata;
    }
}