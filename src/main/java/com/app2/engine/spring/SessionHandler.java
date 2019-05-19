package com.app2.engine.spring;

import com.app2.engine.entity.model.ChatMessage;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionHandler extends StompSessionHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(SessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.setAutoReceipt(true);
        session.subscribe("/topic/public", this);
        Gson gson=new Gson();
        Map user=new HashMap();
        user.put("sender",session.getSessionId());
        user.put("type","JOIN");
        session.send("/app/chat.addUser", gson.toJson(user));

        LOGGER.info("New session: {}", session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.info("Received: {}", ((ChatMessage) payload).getContent());
    }
}
