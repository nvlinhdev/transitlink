package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;
import vn.edu.fpt.transitlink.identity.enumeration.NotificationType;
import vn.edu.fpt.transitlink.identity.mapper.AccountMapper;
import vn.edu.fpt.transitlink.identity.mapper.RoleMapper;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.ImportAccountRequest;
import vn.edu.fpt.transitlink.identity.request.InitiateEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateCurrentUserRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.service.AccountNotificationService;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.service.RoleService;
import vn.edu.fpt.transitlink.identity.service.VerificationService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final AccountNotificationService notificationService;

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#result.id") },
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
    )
    @Override
    public AccountDTO createAccount(CreateAccountRequest dto) {

        String randomPassword = UUID.randomUUID().toString().substring(0, 8);

        Account account = new Account();
        account.setEmail(dto.email());
        account.setPassword(passwordEncoder.encode(randomPassword));
        account.setFirstName(dto.firstName());
        account.setLastName(dto.lastName());
        account.setGender(dto.gender());
        account.setBirthDate(dto.birthDate());
        account.setPhoneNumber(dto.phoneNumber());
        account.setZaloPhoneNumber(dto.zaloPhoneNumber());
        account.setEmailVerified(true);
        account.setProfileCompleted(account.isProfileCompleted());
        if (dto.roles() != null) {
            Set<Role> roles = dto.roles().stream()
                            .map(roleService::findByName)
                            .map(roleMapper::toEntity)
                            .collect(Collectors.toSet());
            account.setRoles(roles);
        }

        Account saved = accountRepository.save(account);

        // Gửi email thông báo tài khoản mới được tạo
        sendAccountCreatedNotification(saved, randomPassword);

        return accountMapper.toDTO(saved);
    }

    @Cacheable(value = "accountsById", key = "#id")
    @Override
    public AccountDTO getAccountById(UUID id) {
        Account acc = accountRepository.findById(id)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toDTO(acc);
    }

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#id") },
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
    )
    @Override
    public AccountDTO updateAccount(UUID id, UpdateAccountRequest dto) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if(dto.email() != null && !dto.email().equalsIgnoreCase(account.getEmail())) {
            // Kiểm tra email mới đã tồn tại chưa
            boolean emailExists = accountRepository.existsByEmail(dto.email());
            if (emailExists) {
                throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS, "Email is already in use");
            }
            account.setEmail(dto.email());
        }

        if(dto.firstName() != null && !dto.firstName().equalsIgnoreCase(account.getFirstName())) {
            account.setFirstName(dto.firstName());
        }

        if(dto.lastName() != null && !dto.lastName().equalsIgnoreCase(account.getLastName())) {
            account.setLastName(dto.lastName());
        }

        if(dto.gender() != null && !dto.gender().equals(account.getGender())) {
            account.setGender(dto.gender());
        }

        if (dto.birthDate() != null && !dto.birthDate().equals(account.getBirthDate())) {
            account.setBirthDate(dto.birthDate());
        }

        if(dto.phoneNumber() != null && !dto.phoneNumber().equalsIgnoreCase(account.getPhoneNumber())) {
            account.setPhoneNumber(dto.phoneNumber());
        }

        if(dto.zaloPhoneNumber() != null && !dto.zaloPhoneNumber().equalsIgnoreCase(account.getZaloPhoneNumber())) {
            account.setZaloPhoneNumber(dto.zaloPhoneNumber());
        }

        account.setEmailVerified(true);
        account.setProfileCompleted(account.isProfileCompleted());
        if (dto.roles() != null) {
            Set<Role> roles = dto.roles().stream()
                    .map(roleService::findByName)
                    .map(roleMapper::toEntity)
                    .collect(Collectors.toSet());
            account.setRoles(roles);
        }
        Account saved = accountRepository.save(account);
        return accountMapper.toDTO(saved);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "accountsById", key = "#deleteId"),
                    @CacheEvict(value = "accountsPage", allEntries = true),
                    @CacheEvict(value = "deletedAccountsPage", allEntries = true)
            }
    )
    @Override
    public AccountDTO deleteAccount(UUID deleteId, UUID deletedBy) {
        if (deleteId.equals(deletedBy)) {
            throw new BusinessException(AuthErrorCode.CANNOT_DELETE_OWN_ACCOUNT, "Cannot delete own account");
        }

        Account account = accountRepository.findById(deleteId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        account.softDelete(deletedBy);
        Account saved = accountRepository.save(account);

        return accountMapper.toDTO(saved);
    }

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#restoreId") },
            evict = {
                    @CacheEvict(value = "accountsPage", allEntries = true),
                    @CacheEvict(value = "deletedAccountsPage", allEntries = true)
            }
    )
    @Override
    public AccountDTO restoreAccount(UUID restoreId) {
        // Dùng query để tìm cả deleted
        Account account = accountRepository.findByIdIncludingDeleted(restoreId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        account.restore();
        Account saved = accountRepository.save(account);

        return accountMapper.toDTO(saved);
    }

    @Cacheable(value = "accountsPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<AccountDTO> getAccounts(int page, int size) {
        var paged = accountRepository.findAllExcludingRoles( Set.of(RoleName.PASSENGER, RoleName.MANAGER),PageRequest.of(page, size));
        return paged.stream().map(accountMapper::toDTO).toList();
    }

    @Override
    public long countAccounts() {
        return accountRepository.count();
    }

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#userId") },
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
    )
    @Override
    public AccountDTO updateCurrentUserAccount(UUID userId, UpdateCurrentUserRequest dto) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // Cập nhật thông tin cá nhân của người dùng
        account.setFirstName(dto.firstName());
        account.setLastName(dto.lastName());
        account.setGender(dto.gender());
        account.setBirthDate(dto.birthDate());
        account.setPhoneNumber(dto.phoneNumber());
        account.setZaloPhoneNumber(dto.zaloPhoneNumber());
        account.setAvatarUrl(dto.avatarUrl());
        account.setProfileCompleted(account.isProfileCompleted());

        Account saved = accountRepository.save(account);
        return accountMapper.toDTO(saved);
    }

    @Override
    public CompletableFuture<Boolean> initiateEmailChange(UUID userId, InitiateEmailChangeRequest dto) {
        // First get the account to verify it exists
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // Check if the new email is different from the current one
        if (account.getEmail().equalsIgnoreCase(dto.newEmail())) {
            throw new BusinessException(AuthErrorCode.INVALID_NEW_EMAIL, "New email must be different from current email");
        }

        // Check if the new email is already in use by another account
        boolean emailExists = accountRepository.existsByEmail(dto.newEmail());
        if (emailExists) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS, "Email is already in use");
        }

        // Add additional template variables
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", account.getFirstName() + " " + account.getLastName());
        templateVariables.put("currentEmail", account.getEmail());
        templateVariables.put("newEmail", dto.newEmail());

        // Send verification email to the new email address
        return verificationService.sendVerificationEmail(dto.newEmail(), VerificationType.EMAIL_CHANGE, templateVariables);
    }

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#userId") },
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
    )
    @Override
    public AccountDTO updateCurrentUserEmail(UUID userId, VerifyEmailChangeRequest dto) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // Verify the OTP for the new email
        boolean isOtpValid = verificationService.verifyOtp(dto.newEmail(), dto.otp(), VerificationType.EMAIL_CHANGE);

        if (!isOtpValid) {
            throw new BusinessException(AuthErrorCode.INVALID_OTP, "Invalid or expired OTP");
        }

        account.setEmail(dto.newEmail());
        account.setEmailVerified(true);

        Account saved = accountRepository.save(account);
        return accountMapper.toDTO(saved);
    }

    @Cacheable(value = "deletedAccountsPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<AccountDTO> getDeletedAccounts(int page, int size) {
        var paged = accountRepository.findAllDeletedExcludingRoles(Set.of(RoleName.PASSENGER, RoleName.MANAGER), PageRequest.of(page, size));
        return paged.stream().map(accountMapper::toDTO).toList();
    }

    @Override
    public long countDeletedAccounts() {
        return accountRepository.countDeletedExcludingRoles(Set.of(RoleName.PASSENGER, RoleName.MANAGER));
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "accountsById", key = "#id"),
                    @CacheEvict(value = "accountsPage", allEntries = true),
                    @CacheEvict(value = "deletedAccountsPage", allEntries = true)
            }
    )
    @Override
    public void hardDeleteAccount(UUID id) {
        // Tìm tài khoản bao gồm cả tài khoản đã bị xóa mềm
        Account account = accountRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // Xóa vĩnh viễn tài khoản
        accountRepository.delete(account);
    }

    @Override
    public AccountDTO getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .map(accountMapper::toDTO)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Override
    public CompletableFuture<Boolean> resendAccountCreatedNotification(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // Tạo mật khẩu tạm thời mới
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return sendAccountCreatedNotification(account, newPassword);
    }

    private CompletableFuture<Boolean> sendAccountCreatedNotification(Account account, String rawPassword) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", account.getFirstName() + " " + account.getLastName());
        templateVariables.put("email", account.getEmail());
        templateVariables.put("password", rawPassword);

        return notificationService.sendNotificationEmail(
            account.getEmail(),
            NotificationType.ACCOUNT_CREATED,
            templateVariables
        );
    }

    @Override
    public List<AccountDTO> importAccounts(List<ImportAccountRequest> importRequests) {
        if (importRequests.isEmpty()) {
            return new ArrayList<>();
        }

        // Bulk check tất cả emails có tồn tại không
        List<String> emails = importRequests.stream()
                .map(ImportAccountRequest::email)
                .toList();

        List<Account> existingAccounts = accountRepository.findByEmailsIn(emails);

        // Tạo map để lookup nhanh accounts có sẵn theo email
        Map<String, Account> existingAccountMap = existingAccounts.stream()
                .collect(Collectors.toMap(
                        Account::getEmail,
                        account -> account,
                        (existing, replacement) -> existing // Keep existing if duplicate
                ));

        List<AccountDTO> results = new ArrayList<>();
        List<Account> newAccountsToSave = new ArrayList<>();
        Map<String, String> accountPasswordMap = new HashMap<>(); // To store raw passwords

        for (ImportAccountRequest importRequest : importRequests) {
            Account existingAccount = existingAccountMap.get(importRequest.email());

            if (existingAccount != null) {
                // Nếu đã tồn tại, sử dụng account có sẵn
                results.add(accountMapper.toDTO(existingAccount));
            } else {
                // Nếu chưa tồn tại, chuẩn bị để bulk insert
                String rawPassword = UUID.randomUUID().toString().substring(0, 8);
                Account newAccount = createNewAccountFromImport(importRequest, rawPassword);
                newAccountsToSave.add(newAccount);
                accountPasswordMap.put(newAccount.getEmail(), rawPassword);
                results.add(accountMapper.toDTO(newAccount));
            }
        }

        // Bulk insert các accounts mới
        if (!newAccountsToSave.isEmpty()) {
            List<Account> savedAccounts = accountRepository.saveAll(newAccountsToSave);

            savedAccounts.stream()
                    .map(account -> {
                        String rawPassword = accountPasswordMap.get(account.getEmail());
                        return sendAccountCreatedNotification(account, rawPassword);
                    })
                    .toList();
        }

        return results;
    }

    private Account createNewAccountFromImport(ImportAccountRequest importRequest, String rawPassword) {
        // Map basic fields
        Account account = accountMapper.toEntity(importRequest);

        // Set manually managed fields
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setEmailVerified(true);
        account.setProfileCompleted(account.isProfileCompleted());

        // Convert and set roles if provided
        if (importRequest.roles() != null && !importRequest.roles().isEmpty()) {
            Set<Role> roles = importRequest.roles().stream()
                    .map(roleService::findByName)
                    .map(roleMapper::toEntity)
                    .collect(Collectors.toSet());
            account.setRoles(roles);
        }

        return account;
    }
}
