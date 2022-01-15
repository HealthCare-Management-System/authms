package com.citiustech.authms;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TokenRequest {
    public static class Builder{
        MultiValueMap<String, String> data;
        public Builder(String clientID, String grant_type){
            data = new LinkedMultiValueMap<>();
            data.put("client_id", Collections.singletonList(clientID));
            data.put("grant_type", Collections.singletonList(grant_type));
        }
        public Builder add(String key, String value){
            data.put(key, Collections.singletonList(value));
            return this;
        }
        public HttpEntity<MultiValueMap<String, String>>  build(){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            return new HttpEntity<>(data, headers);
        }
    }
    private TokenRequest(){
    }
}
