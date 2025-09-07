package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.InitiateEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateCurrentUserRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailChangeRequest;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/identity/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing user accounts")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Create account",
            description = "Create a new account"
    )
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<StandardResponse<AccountDTO>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO result = accountService.createAccount(request);
        return ResponseEntity.status(201).body(StandardResponse.created(result));
    }

    @Operation(summary = "Get account by ID",
            description = "Get account details by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountDTO>> getAccountById(@PathVariable UUID id) {
        AccountDTO result = accountService.getAccountById(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update account",
            description = "Update account details"
    )
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountDTO>> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
        AccountDTO result = accountService.updateAccount(id, request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }


    @Operation(summary = "Permanently delete account",
            description = "Permanently delete an account (cannot be restored). DEPRECATED: This endpoint is deprecated and will be removed in future versions. Use soft delete instead."
    )
    @Deprecated(since = "1.1.0", forRemoval = true)
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<Void>> hardDeleteAccount(@PathVariable UUID id) {
        accountService.hardDeleteAccount(id);
        return ResponseEntity.ok(StandardResponse.success("Account permanently deleted successfully, but note that this endpoint is deprecated and will be removed in future versions.", null));
    }

    @Operation(summary = "Delete account",
            description = "Delete account by ID"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<AccountDTO>> deleteAccount(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable UUID id) {

        AccountDTO deletedAccount = accountService.deleteAccount(id, principal.getId());
        return ResponseEntity.ok(
                StandardResponse.success("Account deleted successfully", deletedAccount)
        );
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<AccountDTO>> restoreAccount(@PathVariable UUID id) {
        AccountDTO restoredAccount = accountService.restoreAccount(id);
        return ResponseEntity.ok(
                StandardResponse.success("Account restored successfully", restoredAccount)
        );
    }

    @Operation(summary = "Get accounts (paginated)",
            description = "Get paginated list of accounts"
    )
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<AccountDTO>> getAccounts(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        List<AccountDTO> accounts = accountService.getAccounts(page, size);
        long total = accountService.countAccounts();
        PaginatedResponse<AccountDTO> response = new PaginatedResponse<>(accounts, page, size, total);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get deleted accounts (paginated)",
            description = "Get paginated list of soft-deleted accounts"
    )
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/deleted")
    public ResponseEntity<PaginatedResponse<AccountDTO>> getDeletedAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AccountDTO> deletedAccounts = accountService.getDeletedAccounts(page, size);
        long total = accountService.countDeletedAccounts();
        PaginatedResponse<AccountDTO> response = new PaginatedResponse<>(deletedAccounts, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's account",
            description = "Get details of the currently authenticated user's account"
    )
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<AccountDTO>> getCurrentUserAccount(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        AccountDTO result = accountService.getAccountById(principal.getId());
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update current user's account",
            description = "Update basic information of the currently authenticated user's account"
    )
    @PutMapping("/me")
    public ResponseEntity<StandardResponse<AccountDTO>> updateCurrentUserAccount(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateCurrentUserRequest request) {
        AccountDTO result = accountService.updateCurrentUserAccount(principal.getId(), request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Initiate email change process",
            description = "Start the email change process by sending a verification code to the new email"
    )
    @PostMapping("/me/email")
    public ResponseEntity<StandardResponse<String>> initiateEmailChange(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody InitiateEmailChangeRequest request) {

        accountService.initiateEmailChange(principal.getId(), request);

        // Not waiting for the email to be sent to respond to the client
        return ResponseEntity.accepted().body(
                StandardResponse.success("Verification email sent to " + request.newEmail() + ". Please check your inbox for the verification code.")
        );
    }

    @Operation(summary = "Complete email change with verification",
            description = "Complete the email change process by verifying the OTP sent to the new email"
    )
    @PutMapping("/me/email")
    public ResponseEntity<StandardResponse<AccountDTO>> verifyAndUpdateEmail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody VerifyEmailChangeRequest request) {

        AccountDTO result = accountService.updateCurrentUserEmail(principal.getId(), request);

        return ResponseEntity.ok(
                StandardResponse.success("Email successfully updated", result)
        );
    }
}
