package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateAccountRequest;
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
        description = "Create a new account",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = CreateAccountRequest.class),
                examples = @ExampleObject(
                    name = "CreateAccountRequest Example",
                    value = "{\"email\": \"user@example.com\", \"password\": \"StrongPassword123\", \"phone\": \"0123456789\", \"fullName\": \"John Doe\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Success Response",
                        value = "{\"success\": true, \"message\": \"Account created successfully\", \"data\": {\"id\": \"b3b8c8e2-8c2e-4e2a-9c2e-8c2e4e2a9c2e\", \"email\": \"user@example.com\", \"phone\": \"0123456789\", \"fullName\": \"John Doe\"}}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Error Response",
                        value = "{\"success\": false, \"message\": \"Invalid request\", \"data\": null}"
                    )
                )
            )
        }
    )
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<StandardResponse<AccountDTO>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO result = accountService.createAccount(request);
        return ResponseEntity.status(201).body(StandardResponse.created(result));
    }

    @Operation(summary = "Get account by ID",
        description = "Get account details by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Account found",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Success Response",
                        value = "{\"success\": true, \"message\": null, \"data\": {\"id\": \"b3b8c8e2-8c2e-4e2a-9c2e-8c2e4e2a9c2e\", \"email\": \"user@example.com\", \"phone\": \"0123456789\", \"fullName\": \"John Doe\"}}"
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Account not found",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Error Response",
                        value = "{\"success\": false, \"message\": \"Account not found\", \"data\": null}"
                    )
                )
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountDTO>> getAccountById(@PathVariable UUID id) {
        AccountDTO result = accountService.getAccountById(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update account",
        description = "Update account details",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = UpdateAccountRequest.class),
                examples = @ExampleObject(
                    name = "UpdateAccountRequest Example",
                    value = "{\"phone\": \"0987654321\", \"fullName\": \"John Doe Updated\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Success Response",
                        value = "{\"success\": true, \"message\": null, \"data\": {\"id\": \"b3b8c8e2-8c2e-4e2a-9c2e-8c2e4e2a9c2e\", \"email\": \"user@example.com\", \"phone\": \"0987654321\", \"fullName\": \"John Doe Updated\"}}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Error Response",
                        value = "{\"success\": false, \"message\": \"Invalid request\", \"data\": null}"
                    )
                )
            )
        }
    )
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountDTO>> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
        AccountDTO result = accountService.updateAccount(id, request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Delete account",
        description = "Delete account by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Success Response",
                        value = "{\"success\": true, \"message\": \"Account deleted successfully\", \"data\": null}"
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Account not found",
                content = @Content(
                    schema = @Schema(implementation = StandardResponse.class),
                    examples = @ExampleObject(
                        name = "Error Response",
                        value = "{\"success\": false, \"message\": \"Account not found\", \"data\": null}"
                    )
                )
            )
        }
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
        description = "Get paginated list of accounts",
        responses = {
            @ApiResponse(responseCode = "200", description = "Accounts list",
                content = @Content(
                    schema = @Schema(implementation = PaginatedResponse.class),
                    examples = @ExampleObject(
                        name = "PaginatedResponse Example",
                        value = "{\"content\": [{\"id\": \"b3b8c8e2-8c2e-4e2a-9c2e-8c2e4e2a9c2e\", \"email\": \"user@example.com\", \"phone\": \"0123456789\", \"fullName\": \"John Doe\"}], \"page\": 0, \"size\": 10, \"total\": 1}"
                    )
                )
            )
        }
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


}
