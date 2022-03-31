package ch.uzh.ifi.hase.soprafs22.websocket.temptesting;

import ch.uzh.ifi.hase.soprafs22.websocket.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class testing {

    UpdateController updCtrl = SpringContext.getBean(UpdateController.class);

    private final Logger log = LoggerFactory.getLogger(UpdateController.class);

    public void test()
    {
        while (true)
        {
            try{

                Thread.sleep(5000);
                log.warn("OK");
                UpdateDTO updto = new UpdateDTO(UpdateType.BOARD, "some message");
                updCtrl.sendUpdateToUser("null", updto);
            }catch (Exception e)
            {
                log.error(e.getMessage());
                return;
            }
        }

    }

}
