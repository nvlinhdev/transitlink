package vn.edu.fpt.transitlink.location.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "places")
public class PlaceDocument {
    @Id
    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
}
