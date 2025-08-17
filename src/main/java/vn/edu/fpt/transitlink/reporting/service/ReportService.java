package vn.edu.fpt.transitlink.reporting.service;

import vn.edu.fpt.transitlink.driver.dto.DriverDTO;
import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;
import vn.edu.fpt.transitlink.reporting.dto.ReportDTO;
import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;
import vn.edu.fpt.transitlink.vehicle.dto.VehicleDTO;

public interface ReportService {

    ReportDTO generateTripReport(ReportDTO reportData, ScheduleDTO scheduleData);
    ReportDTO generatePassengerReport (ReportDTO reportData, PassengerDTO passengerData);
    ReportDTO generateDriverReport (ReportDTO reportData, DriverDTO driverData);
    ReportDTO generateVehicleUsageReport (ReportDTO reportData, VehicleDTO vehicleData);
    ReportDTO generateIncidentReport (ReportDTO reportData);
    ReportDTO exportReport (ReportDTO reportData);
    ReportDTO viewReportsDashboard (ReportDTO reportData);
    ReportDTO filterReportData (ReportDTO reportData);

}
