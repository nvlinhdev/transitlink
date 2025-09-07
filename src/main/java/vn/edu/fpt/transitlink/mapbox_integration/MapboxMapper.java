package vn.edu.fpt.transitlink.mapbox_integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;

import java.util.*;

public class MapboxMapper {

    public static List<PlaceDTO> mapboxToPlaceDTOs(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        List<PlaceDTO> places = new ArrayList<>();

        for (JsonNode feature : root.path("features")) {
            JsonNode geometry = feature.path("geometry");
            double longitude = geometry.path("coordinates").get(0).asDouble();
            double latitude = geometry.path("coordinates").get(1).asDouble();

            JsonNode properties = feature.path("properties");
            String name = properties.path("name").asText("");
            String mapboxId = properties.has("mapbox_id") ? properties.path("mapbox_id").asText() : UUID.randomUUID().toString();
            UUID placeId = UUID.nameUUIDFromBytes(mapboxId.getBytes());

            // Build address per your logic
            JsonNode context = properties.path("context");
            List<String> addressParts = new ArrayList<>();
            if (context.has("address") && context.path("address").has("name")) {
                addressParts.add(context.path("address").path("name").asText());
                if (context.has("neighborhood") && context.path("neighborhood").has("name"))
                    addressParts.add(context.path("neighborhood").path("name").asText());
                if (context.has("place") && context.path("place").has("name"))
                    addressParts.add(context.path("place").path("name").asText());
            } else if (context.has("street") && context.path("street").has("name")) {
                addressParts.add(context.path("street").path("name").asText());
                if (context.has("neighborhood") && context.path("neighborhood").has("name"))
                    addressParts.add(context.path("neighborhood").path("name").asText());
                if (context.has("place") && context.path("place").has("name"))
                    addressParts.add(context.path("place").path("name").asText());
            } else {
                if (context.has("neighborhood") && context.path("neighborhood").has("name"))
                    addressParts.add(context.path("neighborhood").path("name").asText());
                if (context.has("place") && context.path("place").has("name"))
                    addressParts.add(context.path("place").path("name").asText());
            }
            String address = String.join(", ", addressParts);

            places.add(new PlaceDTO(
                    placeId,
                    name,
                    latitude,
                    longitude,
                    address
            ));
        }
        return places;
    }
}