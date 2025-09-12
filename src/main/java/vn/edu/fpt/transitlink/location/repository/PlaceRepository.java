package vn.edu.fpt.transitlink.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.location.entity.Place;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
    Optional<Place> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query("SELECT p FROM Place p WHERE (p.latitude, p.longitude) IN :coordinates")
    List<Place> findByCoordinates(@Param("coordinates") List<Object[]> coordinates);
}
