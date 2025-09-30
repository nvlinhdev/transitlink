package vn.edu.fpt.transitlink.location.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location/places")
@Tag(name = "Place Management", description = "APIs for searching and managing places")
public class PlaceController {
    private final PlaceService placeService;

    @Operation(
        summary = "Search places",
        description = "Search for places by query string"
    )
    @GetMapping
    public ResponseEntity<StandardResponse<List<PlaceDTO>>> searchPlaces(@RequestParam String query) {
        List<PlaceDTO> places = placeService.search(query);
        return ResponseEntity.ok(StandardResponse.success(places));
    }
}
