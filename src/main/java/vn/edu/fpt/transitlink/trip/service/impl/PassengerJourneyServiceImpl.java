package vn.edu.fpt.transitlink.trip.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.trip.mapper.PassengerJourneyMapper;
import vn.edu.fpt.transitlink.trip.repository.PassengerJourneyRepository;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;

@Service
public class PassengerJourneyServiceImpl implements PassengerJourneyService {

    private final PassengerJourneyMapper mapper;

    private final PassengerJourneyRepository passengerJourneyRepository;

    public PassengerJourneyServiceImpl(PassengerJourneyMapper mapper, PassengerJourneyRepository passengerJourneyRepository) {
        this.mapper = mapper;
        this.passengerJourneyRepository = passengerJourneyRepository;
    }
}
