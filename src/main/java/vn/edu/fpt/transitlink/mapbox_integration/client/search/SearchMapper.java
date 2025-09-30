package vn.edu.fpt.transitlink.mapbox_integration.client.search;

import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.Context;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.Feature;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.FeatureCollection;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SearchMapper {

    public static List<PlaceDTO> fromFeatureCollection(FeatureCollection fc) {
        List<PlaceDTO> places = new ArrayList<>();

        for (Feature feature : fc.features()) {
            double longitude = feature.geometry().coordinates().get(0);
            double latitude = feature.geometry().coordinates().get(1);

            Properties properties = feature.properties();
            String name = properties.name();
            String mapboxId = (properties.mapboxId() != null && !properties.mapboxId().isBlank())
                    ? properties.mapboxId()
                    : UUID.randomUUID().toString();
            UUID placeId = UUID.nameUUIDFromBytes(mapboxId.getBytes());

            // build address giữ nguyên logic cũ
            Context context = properties.context();
            List<String> addressParts = new ArrayList<>();

            if (context != null && context.address() != null && context.address().name() != null) {
                addressParts.add(context.address().name());
                if (context.neighborhood() != null && context.neighborhood().name() != null)
                    addressParts.add(context.neighborhood().name());
                if (context.place() != null && context.place().name() != null)
                    addressParts.add(context.place().name());
            } else if (context != null && context.street() != null && context.street().name() != null) {
                addressParts.add(context.street().name());
                if (context.neighborhood() != null && context.neighborhood().name() != null)
                    addressParts.add(context.neighborhood().name());
                if (context.place() != null && context.place().name() != null)
                    addressParts.add(context.place().name());
            } else {
                if (context != null && context.neighborhood() != null && context.neighborhood().name() != null)
                    addressParts.add(context.neighborhood().name());
                if (context != null && context.place() != null && context.place().name() != null)
                    addressParts.add(context.place().name());
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
