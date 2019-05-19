package com.app2.engine.Service.linenotify;


import com.app2.engine.util.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbstractLineNotifyService {
    private static final Logger LOGGER = LogManager.getLogger(AbstractLineNotifyService.class);

    @Autowired
    RestTemplate restTemplate;


    private Integer RESPONSE_TIME = 3000;

    @Value("${line-notify-server}")
    private String LineNotifyServer;

    private String notifyToken;

    public String sentPostJsonString(String url, String json) {
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(RESPONSE_TIME);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers = setHeaderUserMapDetails(headers);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        ResponseEntity<String> reponseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return reponseEntity.getBody();
    }

    public String sentMessage(String message) {
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(RESPONSE_TIME);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers = setHeaderUserMapDetails(headers);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("message", message);
        HttpEntity<?> entity = new HttpEntity<Object>(body, headers);
        ResponseEntity<String> reponseEntity = restTemplate.exchange(LineNotifyServer, HttpMethod.POST, entity, String.class);
        return reponseEntity.getBody();
    }

    public String sentMessage(String message, String picturePath) {
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(RESPONSE_TIME);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers = setHeaderUserMapDetails(headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        if (BeanUtils.isNotNull(message)) {
            body.add("message", message);
        } else {
            body.add("message", "-");
        }
        body.add("imageFile", new FileSystemResource(picturePath));
        HttpEntity<?> entity = new HttpEntity<Object>(body, headers);
        ResponseEntity<String> reponseEntity = restTemplate.exchange(LineNotifyServer, HttpMethod.POST, entity, String.class);
        return reponseEntity.getBody();
    }

    private HttpHeaders setHeaderUserMapDetails(HttpHeaders headers) {
//        DbParameter parameterToken = dbParameterRepository.findByPgmSetup("LINE_TOKEN");
//        if(BeanUtils.isNotNull(parameterToken)){
//            notifyToken = parameterToken.getParameterValue();
//        }
        headers.add("authorization", "Bearer " + this.notifyToken);
        return headers;
    }


    public String getMessageWithToken(String message, String token) {
        this.notifyToken = token;
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(RESPONSE_TIME);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers = setHeaderUserMapDetails(headers);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("message", message);
        HttpEntity<?> entity = new HttpEntity<Object>(body, headers);
        ResponseEntity<String> reponseEntity = restTemplate.exchange(LineNotifyServer, HttpMethod.POST, entity, String.class);
        return reponseEntity.getBody();
    }





    /***********************************************************************************************
    *
    *                               Async Method
    *
    *************************************************************************************************/
    //  Async Restemplate
    public ListenableFuture<ResponseEntity<String>> getAsyncMessageWithToken(String message, String token) {
        try {
            String url = "http://localhost:9999/VCCJobEngine/api/lineNotifyControllers/sentMessageWithToken?" +
                    "&message=" + message +
                    "&token=" + token;

            AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Content-Type", "application/json; charset=utf-8");
            HttpEntity<String> entity = new HttpEntity<String>("", headers);
            return asyncRestTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    //  Async Restemplate
    public ListenableFuture<ResponseEntity<String>> postAsyncMessageWithToken(String json) {
        try {
            String url = "http://localhost:9999/VCCJobEngine/api/lineNotifyControllers/sentMessageWithToken";

            AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Content-Type", "application/json; charset=utf-8");
            HttpEntity<String> entity = new HttpEntity<String>(json, headers);
            return asyncRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
