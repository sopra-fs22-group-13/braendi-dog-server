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

package ch.uzh.ifi.hase.soprafs22.websocket.dto;

import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import org.springframework.web.util.HtmlUtils;
import org.springframework.stereotype.Controller;

/**
 * The content of an update, with a type and a message that should be formatted in JSON.
 */
public class UpdateDTO {
    private UpdateType type;
    private String message;

    public UpdateDTO(){}

    public UpdateDTO(UpdateType type, String message)
    {
        this.type = type;
        this.message = message;
    }

    public UpdateType getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }
}
