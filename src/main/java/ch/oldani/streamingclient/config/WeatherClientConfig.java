package ch.oldani.streamingclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weather-client")
public class WeatherClientConfig {

   private String baseUrl;
   private String streamingUrl;
   private Duration timeout;
}
