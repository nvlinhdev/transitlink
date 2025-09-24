package vn.edu.fpt.transitlink.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.location.entity.DriverLocation;

import java.util.UUID;

public interface DriverLocationRepository extends JpaRepository<DriverLocation, UUID> {

}
