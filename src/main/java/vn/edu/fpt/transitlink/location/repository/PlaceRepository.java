package vn.edu.fpt.transitlink.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.location.entity.Place;

import java.util.List;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
    List<Place> findByNameContainingIgnoreCase(String query);
}
