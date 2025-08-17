package vn.edu.fpt.transitlink.trip.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.trip.mapper.TripMapper;
import vn.edu.fpt.transitlink.trip.repository.DropoffRepository;
import vn.edu.fpt.transitlink.trip.repository.PickupRepository;
import vn.edu.fpt.transitlink.trip.repository.TripCheckoutRepository;
import vn.edu.fpt.transitlink.trip.repository.TripCheckinRepository;
import vn.edu.fpt.transitlink.trip.service.TripService;

@Service
public class TripServiceImpl implements TripService {
    private final TripMapper mapper;

    private TripCheckinRepository tripCheckinRepository;
    private  TripCheckoutRepository tripCheckoutRepository;
    private  DropoffRepository dropoffRepository;
    private  PickupRepository pickupRepository;


    public TripServiceImpl(TripMapper mapper, TripCheckinRepository tripCheckinRepository) {
        this.mapper = TripMapper.INSTANCE;
    }

    public TripServiceImpl(TripMapper mapper, TripCheckoutRepository tripCheckoutRepository) {
        this.mapper = TripMapper.INSTANCE;
        this.tripCheckoutRepository = tripCheckoutRepository;
    }

    public TripServiceImpl(TripMapper mapper, DropoffRepository dropoffRepository) {
        this.mapper = TripMapper.INSTANCE;
        this.dropoffRepository = dropoffRepository;
    }

    public TripServiceImpl(TripMapper mapper, PickupRepository pickupRepository) {
        this.mapper = TripMapper.INSTANCE;
        this.pickupRepository = pickupRepository;
    }
}
