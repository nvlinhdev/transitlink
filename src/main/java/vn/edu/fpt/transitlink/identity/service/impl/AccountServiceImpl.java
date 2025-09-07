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
import vn.edu.fpt.transitlink.identity.mapper.AccountMapper;
import vn.edu.fpt.transitlink.identity.mapper.RoleMapper;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.InitiateEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateCurrentUserRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailChangeRequest;
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

    @Caching(
            put   = { @CachePut(value = "accountsById", key = "#result.id") },
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
    )
    @Override
    public AccountDTO createAccount(CreateAccountRequest dto) {
        Account account = new Account();
        account.setEmail(dto.email());
        account.setPassword(passwordEncoder.encode(dto.password()));
        account.setFirstName(dto.firstName());
        account.setLastName(dto.lastName());
        account.setGender(dto.gender());
        account.setBirthDate(dto.birthDate());
        account.setPhoneNumber(dto.phoneNumber());
        account.setZaloPhoneNumber(dto.zaloPhoneNumber());
        account.setAvatarUrl(dto.avatarUrl());
        account.setEmailVerified(false);
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
        account.setFirstName(dto.firstName());
        account.setLastName(dto.lastName());
        account.setGender(dto.gender());
        account.setBirthDate(dto.birthDate());
        account.setPhoneNumber(dto.phoneNumber());
        account.setZaloPhoneNumber(dto.zaloPhoneNumber());
        account.setAvatarUrl(dto.avatarUrl());
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
}
