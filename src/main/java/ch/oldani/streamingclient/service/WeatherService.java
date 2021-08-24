package ch.oldani.streamingclient.service;

import ch.oldani.streamingclient.client.LocationClient;
import ch.oldani.streamingclient.client.WeatherClient;
import ch.oldani.streamingclient.domain.WeatherEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class WeatherService {

   private final WeatherClient weatherClient;
   private final LocationClient locationClient;

   public WeatherService(WeatherClient weatherClient, LocationClient locationClient) {
      this.weatherClient = weatherClient;
      this.locationClient = locationClient;
   }

   public Flux<WeatherEvent> streamWeather() {
      return weatherClient.streamWeather()
                          .flatMap(locationClient::getLocation);
   }
}
