package vn.edu.fpt.transitlink.identity.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.request.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountService {
    AccountDTO createAccount(CreateAccountRequest dto);
    AccountDTO getAccountById(UUID id);
    AccountDTO updateAccount(UUID id, UpdateAccountRequest dto);
    AccountDTO deleteAccount(UUID deleteId, UUID deleteByID);
    AccountDTO restoreAccount(UUID restoreId);
    void hardDeleteAccount(UUID id);
    List<AccountDTO> getAccounts(int page, int size);
    long countAccounts();
    AccountDTO updateCurrentUserAccount(UUID userId, UpdateCurrentUserRequest dto);

    // Email change workflow methods
    CompletableFuture<Boolean> initiateEmailChange(UUID userId, InitiateEmailChangeRequest dto);
    AccountDTO updateCurrentUserEmail(UUID userId, VerifyEmailChangeRequest dto);

    // Phương thức mới để lấy danh sách các tài khoản đã bị xóa mềm
    List<AccountDTO> getDeletedAccounts(int page, int size);
    long countDeletedAccounts();

    AccountDTO getAccountByEmail(@NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email format") String email);

    // Phương thức gửi lại email thông báo tài khoản được tạo
    CompletableFuture<Boolean> resendAccountCreatedNotification(UUID accountId);

    List<AccountDTO> importAccounts(List<ImportAccountRequest> importRequests);
}
