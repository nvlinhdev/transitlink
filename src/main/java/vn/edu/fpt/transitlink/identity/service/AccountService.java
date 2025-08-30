package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.InitiateEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateCurrentUserRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailChangeRequest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountService {
    AccountDTO createAccount(CreateAccountRequest dto);
    AccountDTO getAccountById(UUID id);
    AccountDTO updateAccount(UUID id, UpdateAccountRequest dto);
    AccountDTO deleteAccount(UUID deleteId, UUID deleteByID);
    AccountDTO restoreAccount(UUID restoreId);
    List<AccountDTO> getAccounts(int page, int size);
    long countAccounts();
    AccountDTO updateCurrentUserAccount(UUID userId, UpdateCurrentUserRequest dto);

    // Email change workflow methods
    CompletableFuture<Boolean> initiateEmailChange(UUID userId, InitiateEmailChangeRequest dto);
    AccountDTO updateCurrentUserEmail(UUID userId, VerifyEmailChangeRequest dto);
}
