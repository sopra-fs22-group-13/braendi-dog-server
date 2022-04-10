package ch.uzh.ifi.hase.soprafs22.websocket;

import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringContextTest {

    @Test
    void getBean() {
        assertTrue(SpringContext.getBean(UserRepository.class) instanceof UserRepository); //this is a mess as it is some Spring stuff and not a normal class, just do instanceof
        assertEquals(UpdateController.class, SpringContext.getBean(UpdateController.class).getClass()); //compare that the class is correct
    }
}