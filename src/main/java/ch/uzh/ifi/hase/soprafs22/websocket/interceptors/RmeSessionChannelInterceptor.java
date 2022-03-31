package ch.uzh.ifi.hase.soprafs22.websocket.interceptors;

import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.MultiValueMap;

public class RmeSessionChannelInterceptor implements ChannelInterceptor {
    private final Logger log = LoggerFactory.getLogger(UpdateController.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {log.warn("Channel Interceptor");

        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        MultiValueMap<String, String> multiValueMap =
                headers.get(StompHeaderAccessor.NATIVE_HEADERS,MultiValueMap.class);

        if(multiValueMap != null)
        {
            String authString = multiValueMap.getFirst("Authentication");
            log.error(authString);
        }


        return message;
    }
}
