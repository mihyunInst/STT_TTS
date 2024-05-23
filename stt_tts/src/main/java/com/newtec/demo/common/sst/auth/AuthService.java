package com.newtec.demo.common.sst.auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;


@PropertySource("classpath:/config.properties")
public class AuthService {

	
	@Value("${my.stt.client.id}")
	private String clientId;
	
	@Value("${my.stt.client.secret}")
	private String clientSecret;	

	
	public static String getAccessToken() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String formBody = "client_id={YOUR_CLIENT_ID}&client_secret={YOUR_CLIENT_SECRET}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.vito.ai/v1/authenticate"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> map = objectMapper.readValue(response.body(), HashMap.class);

        return map.get("access_token");
    }
}
