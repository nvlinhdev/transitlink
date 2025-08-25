package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.*;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountDTO createAccount(CreateAccountRequest dto);
    AccountDTO getAccountById(UUID id);
    AccountDTO updateAccount(UUID id, UpdateAccountRequest dto);
    AccountDTO deleteAccount(UUID deleteId, UUID deleteByID);
    AccountDTO restoreAccount(UUID restoreId);
    List<AccountDTO> getAccounts(int page, int size);
    long countAccounts();
}
