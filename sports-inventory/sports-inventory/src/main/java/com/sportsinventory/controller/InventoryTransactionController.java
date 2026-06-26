package com.sportsinventory.controller;

import com.sportsinventory.dto.request.CreateInventoryTransactionRequest;
import com.sportsinventory.dto.response.InventoryTransactionResponse;
import com.sportsinventory.entity.InventoryTransaction;
import com.sportsinventory.service.InventoryTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    @PostMapping
    public ResponseEntity<InventoryTransactionResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateInventoryTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(userDetails.getUsername(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<InventoryTransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getMyTransactions(userDetails.getUsername(), pageable));
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<InventoryTransactionResponse> getMyTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getMyTransaction(userDetails.getUsername(), id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryTransactionResponse>> getByStatus(
            @PathVariable InventoryTransaction.TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getByStatus(status));
    }

    @GetMapping("/location/{storageLocationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryTransactionResponse>> getByLocation(
            @PathVariable Long storageLocationId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getByLocation(storageLocationId, pageable));
    }

    @GetMapping("/period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryTransactionResponse>> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(transactionService.getByPeriod(from, to));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryTransactionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam InventoryTransaction.TransactionStatus status) {
        return ResponseEntity.ok(transactionService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<InventoryTransactionResponse> cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.cancelTransaction(userDetails.getUsername(), id));
    }
}
