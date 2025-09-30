package vn.edu.fpt.transitlink.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.location.entity.PassengerLocation;

import java.util.UUID;

public interface PassengerLocationRepository extends JpaRepository<PassengerLocation, UUID> {

}
