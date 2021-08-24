package ch.oldani.streamingclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "location-client")
public class LocationClientConfig {

   private String baseUrl;
   private String geocodeUrl;
   private String googleApiKey;
   private Duration timeout;
}
