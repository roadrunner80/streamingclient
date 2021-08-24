package ch.oldani.streamingclient.client;

import ch.oldani.streamingclient.config.LocationClientConfig;
import ch.oldani.streamingclient.domain.Location;
import ch.oldani.streamingclient.domain.WeatherEvent;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class LocationClient {

   private final WebClient webClient;
   private final LocationClientConfig locationClientConfig;

   public LocationClient(WebClient.Builder builder, LocationClientConfig locationClientConfig) {
      this.webClient = createClient(builder, locationClientConfig);
      this.locationClientConfig = locationClientConfig;
   }

   public Mono<WeatherEvent> getLocation(WeatherEvent weatherEvent) {
      //if (new BigDecimal("7.80228").equals(weatherEvent.getLocation().getLon()) && new BigDecimal("47.47073").equals(weatherEvent.getLocation().getLat())) {
      //   return Mono.just(weatherEvent.toBuilder()
      //                                .location(Location.builder()
      //                                                  .lon(weatherEvent.getLocation().getLon())
      //                                                  .lat(weatherEvent.getLocation().getLat())
      //                                                  .address("Auweg 10 - 4450 Sissach")
      //                                                  .build())
      //                                .build());
      //} else if (new BigDecimal("7.8482").equals(weatherEvent.getLocation().getLon()) && new BigDecimal("47.46183").equals(weatherEvent.getLocation().getLat())) {
      //   return Mono.just(weatherEvent.toBuilder()
      //                                .location(Location.builder()
      //                                                  .lon(weatherEvent.getLocation().getLon())
      //                                                  .lat(weatherEvent.getLocation().getLat())
      //                                                  .address("Rohrbachweg 16, 4460 Gelterkinden")
      //                                                  .build())
      //                                .build());
      //} else {
      //   return Mono.just(weatherEvent.toBuilder()
      //                                .location(Location.builder()
      //                                                  .lon(weatherEvent.getLocation().getLon())
      //                                                  .lat(weatherEvent.getLocation().getLat())
      //                                                  .address(">UNKNOWN<")
      //                                                  .build())
      //                                .build());
      //}
      return webClient.get()
                      .uri(uriBuilder -> createUri(uriBuilder, weatherEvent.getLocation()))
                      .accept(MediaType.APPLICATION_JSON)
                      .retrieve()
                      .bodyToMono(JsonNode.class)
                      .timeout(locationClientConfig.getTimeout())
                      .onErrorResume(err -> null)
                      .doOnNext(location -> log.debug("received location >{}<", location.toPrettyString()))
                      .map(location -> enrichLocation(location, weatherEvent));
   }

   private WeatherEvent enrichLocation(JsonNode location, WeatherEvent weatherEvent) {
      return weatherEvent.toBuilder()
                         .location(weatherEvent.getLocation()
                                               .toBuilder()
                                               .address(extractAddress(location))
                                               .build())
                         .build();
   }

   private String extractAddress(JsonNode location) {
      if (Objects.nonNull(location) && location.isObject() && location.hasNonNull("results")) {
         JsonNode results = location.get("results");
         if (results.isArray()) {
            return StreamSupport.stream(results.spliterator(), false)
                                .filter(elt -> elt.hasNonNull("types"))
                                .filter(elt -> elt.get("types")
                                                  .isArray())
                                .filter(elt -> StreamSupport.stream(elt.get("types")
                                                                       .spliterator(), false)
                                                            .anyMatch(type -> "street_address".equals(type.asText())))
                                .map(elt -> elt.get("formatted_address"))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .map(JsonNode::asText)
                                .orElse(null);
         }
      }
      return null;
   }

   private URI createUri(UriBuilder uriBuilder, Location location) {
      return uriBuilder.path(this.locationClientConfig.getGeocodeUrl())
                       .queryParam("latlng", location.getLat() + "," + location.getLon())
                       .queryParam("key", this.locationClientConfig.getGoogleApiKey())
                       .build();
   }

   private static WebClient createClient(WebClient.Builder builder, LocationClientConfig locationClientConfig) {
      return builder.baseUrl(locationClientConfig.getBaseUrl())
                    .build();
   }
}
