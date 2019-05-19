package com.app2.engine.Service.SensorRange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SensorRangeService {
    private static final Logger LOGGER = LogManager.getLogger(SensorRangeService.class);

    @Async
    public CompletableFuture<String> findUser(String user) {
        try {

            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(10000L);
            String results = "pass";
            return CompletableFuture.completedFuture(results);
        } catch (Exception e) {
            LOGGER.error("Error {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
