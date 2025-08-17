package vn.edu.fpt.transitlink.reporting.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportDTO() {
     UUID id;
     String type;
     String data;
     OffsetDateTime generatedAt;

}
