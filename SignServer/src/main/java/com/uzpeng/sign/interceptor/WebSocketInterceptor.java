package com.uzpeng.sign.interceptor;

import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.SessionStoreKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 */
public class WebSocketInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        logger.info("---------------beforeHandshake");
        SessionAttribute sessionAttribute = (SessionAttribute)attributes.get(SessionStoreKey.KEY_AUTH);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        logger.info("++++++++++++++++++afterHandshake");
    }
}
