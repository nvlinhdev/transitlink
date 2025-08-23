package vn.edu.fpt.transitlink.location.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.mapper.PlaceMapper;
import vn.edu.fpt.transitlink.location.repository.PlaceRepository;
import vn.edu.fpt.transitlink.location.service.PlaceService;

@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceMapper mapper;

    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceMapper mapper, PlaceRepository placeRepository) {
        this.mapper = mapper;
        this.placeRepository = placeRepository;
    }
}
