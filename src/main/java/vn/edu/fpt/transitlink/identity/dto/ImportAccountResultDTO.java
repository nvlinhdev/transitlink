package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.List;

public record ImportAccountResultDTO(
        int successful,
        int failed,
        List<AccountDTO> successfulList,
        List<ImportErrorDTO> failedList
) {
}
