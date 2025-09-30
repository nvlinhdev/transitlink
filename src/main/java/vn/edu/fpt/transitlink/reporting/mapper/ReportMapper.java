package vn.edu.fpt.transitlink.reporting.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.reporting.dto.ReportDTO;
import vn.edu.fpt.transitlink.reporting.entity.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    public static final ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    public ReportDTO toDTO(Report report);

}
