package ch.oldani.streamingclient.client;

import ch.oldani.streamingclient.config.WeatherClientConfig;
import ch.oldani.streamingclient.domain.WeatherEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class WeatherClient {

   private final WebClient webClient;
   private final WeatherClientConfig weatherClientConfig;

   public WeatherClient(WebClient.Builder builder, WeatherClientConfig weatherClientConfig) {
      this.webClient = createClient(builder, weatherClientConfig);
      this.weatherClientConfig = weatherClientConfig;
   }

   public Flux<WeatherEvent> streamWeather() {
      return webClient.get()
                      .uri(weatherClientConfig.getBaseUrl() + weatherClientConfig.getStreamingUrl())
                      .accept(MediaType.APPLICATION_NDJSON)
                      .retrieve()
                      .bodyToFlux(WeatherEvent.class)
                      .timeout(weatherClientConfig.getTimeout())
                      .doOnNext(weatherEvent -> log.debug("received weather event >{}<", weatherEvent))
                      .doOnComplete(() -> log.debug("completed request to weather streaming server"));
   }

   private static WebClient createClient(WebClient.Builder builder, WeatherClientConfig weatherClientConfig) {
      return builder.baseUrl(weatherClientConfig.getBaseUrl())
                    .build();
   }
}
