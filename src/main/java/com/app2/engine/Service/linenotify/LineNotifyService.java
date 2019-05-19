package com.app2.engine.Service.linenotify;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

@Service
public class LineNotifyService extends AbstractLineNotifyService {
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> sendMessage(String message) {
        return CompletableFuture.completedFuture(sentMessage(message));
    }

    @Async("threadPoolTaskExecutor")
    public ListenableFuture<ResponseEntity<String>> sentAsyncMessageWithToken(String message, String token) {
        return getAsyncMessageWithToken(message, token);
    }

    @Async("threadPoolTaskExecutor")
    public ListenableFuture<ResponseEntity<String>> sentAsyncMessageWithToken(String json) {
        return postAsyncMessageWithToken(json);
    }

    public String sendMessageWithToken(String message, String token) {
        return getMessageWithToken(message, token);
    }

    public void sendMessage(String message, String path) {
        sentMessage(message, path);
    }

    public void sendImage(String path) {
        sentMessage(null, path);
    }
}
