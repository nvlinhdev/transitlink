package vn.edu.fpt.transitlink.location.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "places")
@Setting(settingPath = "/elasticsearch/vi_folded-analyzer.json")
public class PlaceDocument {
    @Id
    private UUID id;
    @Field(analyzer = "vi_folded")
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
}
