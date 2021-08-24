package ch.oldani.streamingclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class StreamingClientApplication {

   public static void main(String[] args) {
      SpringApplication.run(StreamingClientApplication.class, args);
   }

}
