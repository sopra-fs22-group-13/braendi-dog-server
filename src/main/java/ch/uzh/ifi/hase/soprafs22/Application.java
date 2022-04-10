package ch.uzh.ifi.hase.soprafs22;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);

    //testing vc here. comment this try block out if it does not work.
      /*try {
          Thread.sleep(10000);
          VoiceRoom vr =  new VoiceChatCreator().createRoomWithPlayers("hello");
          IUpdateController updCtrl = SpringContext.getBean(UpdateController.class);
          UpdateDTO updto = new UpdateDTO(UpdateType.START, String.format("{\"app_id\": \"%s\", \"user_id\": \"%s\", \"user_auth\": \"%s\", \"room_id\": \"%s\"}", vr.appId, vr.player1.id, vr.player1.accessToken, vr.roomId));
          UpdateDTO updto1 = new UpdateDTO(UpdateType.START, String.format("{\"app_id\": \"%s\", \"user_id\": \"%s\", \"user_auth\": \"%s\", \"room_id\": \"%s\"}", vr.appId, vr.player2.id, vr.player2.accessToken, vr.roomId));

          Thread.sleep(2000);

          updCtrl.sendUpdateToUser("12345", updto);
          updCtrl.sendUpdateToUser("123", updto1);

          Thread.sleep(30000);
          new VoiceChatCreator().destroyRoomWithPlayers("hello");
      }
      catch (InterruptedException e) {
          e.printStackTrace();
      }*/
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public String helloWorld() {
    return "The application is running.";
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
      }
    };
  }
}
