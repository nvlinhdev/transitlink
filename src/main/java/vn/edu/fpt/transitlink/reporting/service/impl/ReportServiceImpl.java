package vn.edu.fpt.transitlink.reporting.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.dto.DriverDTO;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.reporting.dto.ReportDTO;
import vn.edu.fpt.transitlink.reporting.mapper.ReportMapper;
import vn.edu.fpt.transitlink.reporting.repository.ReportRepository;
import vn.edu.fpt.transitlink.reporting.service.ReportService;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportMapper mapper;

    private final ReportRepository reportRepository;


    public ReportServiceImpl(ReportMapper mapper, ReportRepository reportRepository) {
        this.mapper = ReportMapper.INSTANCE;
        this.reportRepository = reportRepository;
    }

    @Override
    public ReportDTO generateTripReport(ReportDTO reportData) {
        return null;
    }

    @Override
    public ReportDTO generatePassengerReport(ReportDTO reportData, PassengerDTO passengerData) {
        return null;
    }

    @Override
    public ReportDTO generateDriverReport(ReportDTO reportData, DriverDTO driverData) {
        return null;
    }

    @Override
    public ReportDTO generateVehicleUsageReport(ReportDTO reportData, VehicleDTO vehicleData) {
        return null;
    }

    @Override
    public ReportDTO generateIncidentReport(ReportDTO reportData) {
        return null;
    }

    @Override
    public ReportDTO exportReport(ReportDTO reportData) {
        return null;
    }

    @Override
    public ReportDTO viewReportsDashboard(ReportDTO reportData) {
        return null;
    }

    @Override
    public ReportDTO filterReportData(ReportDTO reportData) {
        return null;
    }
}
