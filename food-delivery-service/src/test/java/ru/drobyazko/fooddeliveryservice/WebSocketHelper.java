package ru.drobyazko.fooddeliveryservice;

import org.apache.tomcat.websocket.Constants;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketHelper {
    private static final Long DEFAULT_WEBSOCKET_CONNECTION_TIMEOUT = 10L;

    public static StompSession connectToWebSocket(String port) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, Object> userProperties = new HashMap<>();
        userProperties.put(Constants.WS_AUTHENTICATION_USER_NAME, TestUsers.USER.getUsername());
        userProperties.put(Constants.WS_AUTHENTICATION_PASSWORD, TestUsers.USER.getPassword());
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        standardWebSocketClient.setUserProperties(userProperties);
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(standardWebSocketClient);

        CompletableFuture<StompSession> future = webSocketStompClient.connectAsync(
                "ws://localhost:" + port + "/ws",
                new WebSocketHttpHeaders(),
                new StompSessionHandlerAdapter() {
                });
        return future.get(DEFAULT_WEBSOCKET_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
    }

    public static void subscribeToWebSocket(StompSession stompSession, String destination, Runnable runnable) {
        stompSession.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                runnable.run();
            }
        });
    }
}
