package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.mapper.AccountMapper;
import vn.edu.fpt.transitlink.identity.mapper.RoleMapper;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.service.RoleService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

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
                    @CacheEvict(value = "accountsPage", allEntries = true)
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
            evict = { @CacheEvict(value = "accountsPage", allEntries = true) }
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
}
