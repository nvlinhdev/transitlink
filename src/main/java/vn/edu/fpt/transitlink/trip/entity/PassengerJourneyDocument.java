package vn.edu.fpt.transitlink.trip.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "passenger_journeys")
@Setting(settingPath = "/elasticsearch/vi_folded-analyzer.json")
public class PassengerJourneyDocument {
    @Id
    private UUID id;

    @Field(type = FieldType.Keyword)
    private UUID passengerId;

    @Field(type = FieldType.Text, analyzer = "vi_folded")
    private String passengerName;

    @Field(type = FieldType.Text, analyzer = "vi_folded")
    private String passengerEmail;

    @Field(type = FieldType.Keyword)
    private UUID pickupPlaceId;

    @Field(type = FieldType.Text, analyzer = "vi_folded")
    private String pickupPlaceName;

    @Field(type = FieldType.Keyword)
    private UUID dropoffPlaceId;

    @Field(type = FieldType.Text, analyzer = "vi_folded")
    private String dropoffPlaceName;

    @Field(type = FieldType.Keyword)
    private UUID routeId;

    @Field(type = FieldType.Date)
    private OffsetDateTime lastestStopArrivalTime;

    @Field(type = FieldType.Date)
    private OffsetDateTime actualPickupTime;

    @Field(type = FieldType.Date)
    private OffsetDateTime actualDropoffTime;

    @Field(type = FieldType.Integer)
    private Integer seatCount;

    @Field(type = FieldType.Keyword)
    private String status;
}
