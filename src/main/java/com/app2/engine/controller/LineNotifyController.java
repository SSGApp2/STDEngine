package com.app2.engine.controller;

import com.app2.engine.Service.linenotify.AbstractLineNotifyService;
import com.app2.engine.Service.linenotify.LineNotifyService;
import com.app2.engine.entity.vcc.iot.IotSensorRange;
import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
import com.app2.engine.repository.IotSensorRangeLogRepository;
import com.google.gson.Gson;
import flexjson.JSONSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lineNotifyControllers")
public class LineNotifyController {
    private static final Logger LOGGER = LogManager.getLogger(LineNotifyController.class);
    @Autowired
    LineNotifyService lineNotifyService;

    @Autowired
    private IotSensorRangeLogRepository iotSensorRangeLogRepository;

    @GetMapping("sentAsyncMessageWithToken")
    public void sendMessageAsync(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "token") String token
    ) {
        lineNotifyService.sentAsyncMessageWithToken("ทดสอบ", token).addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Error 1 : {} ", throwable.getMessage(), throwable);
            }

            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                LOGGER.debug("Time 1 {}",System.currentTimeMillis());
            }
        });
        Gson gson = new Gson();
        Map result=new HashMap();
        result.put("message",message);
        result.put("token",token);
        lineNotifyService.postAsyncMessageWithToken(gson.toJson(result)).addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Error 2 : {} ", throwable.getMessage(), throwable);
            }

            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                LOGGER.debug("Time 2 {}",System.currentTimeMillis());
            }
        });
    }


    @GetMapping("sentMessageWithToken")
    public ResponseEntity<String> sentMessageWithToken(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "token") String token

    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("IOT", "1");
        try {
            return new ResponseEntity<String>(lineNotifyService.sendMessageWithToken(message, token), headers, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("error : {}", e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("sentMessageWithToken")
    public ResponseEntity<String> postMessageWithToken(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
            Gson gson = new Gson();
            Map<String, String> mapping = gson.fromJson(json, Map.class);
            String response = lineNotifyService.sendMessageWithToken(mapping.get("message"), mapping.get("token"));
            mapping.put("response", response);
            return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").deepSerialize(mapping), headers, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("error : {}", e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
