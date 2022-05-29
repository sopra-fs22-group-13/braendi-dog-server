/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

/**
 * This class is currently not used, but would be used to do authentication over websocket. This will probably not be necessary as we just send it to a path with the users authtoken from REST.
 */
/*public class RmeSessionChannelInterceptor implements ChannelInterceptor {
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
}*/
