package vn.edu.fpt.transitlink.reporting.service;

import vn.edu.fpt.transitlink.identity.dto.DriverDTO;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.reporting.dto.ReportDTO;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;

public interface ReportService {

    ReportDTO generateTripReport(ReportDTO reportData);
    ReportDTO generatePassengerReport (ReportDTO reportData, PassengerDTO passengerData);
    ReportDTO generateDriverReport (ReportDTO reportData, DriverDTO driverData);
    ReportDTO generateVehicleUsageReport (ReportDTO reportData, VehicleDTO vehicleData);
    ReportDTO generateIncidentReport (ReportDTO reportData);
    ReportDTO exportReport (ReportDTO reportData);
    ReportDTO viewReportsDashboard (ReportDTO reportData);
    ReportDTO filterReportData (ReportDTO reportData);

}
