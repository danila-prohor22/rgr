package com.sportsinventory.service;

import com.sportsinventory.dto.request.CreateInventoryTransactionRequest;
import com.sportsinventory.dto.response.InventoryTransactionResponse;
import com.sportsinventory.entity.Equipment;
import com.sportsinventory.entity.InventoryTransaction;
import com.sportsinventory.entity.InventoryTransactionItem;
import com.sportsinventory.entity.StorageLocation;
import com.sportsinventory.entity.User;
import com.sportsinventory.exception.ResourceNotFoundException;
import com.sportsinventory.repository.EquipmentRepository;
import com.sportsinventory.repository.InventoryTransactionRepository;
import com.sportsinventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EquipmentService equipmentService;
    private final EquipmentRepository equipmentRepository;
    private final StorageLocationService storageLocationService;

    @Transactional
    public InventoryTransactionResponse createTransaction(String userEmail, CreateInventoryTransactionRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        StorageLocation location = storageLocationService.findLocation(request.getStorageLocationId());

        InventoryTransaction transaction = InventoryTransaction.builder()
                .user(user)
                .storageLocation(location)
                .type(request.getType())
                .status(InventoryTransaction.TransactionStatus.CREATED)
                .responsiblePerson(request.getResponsiblePerson())
                .comment(request.getComment())
                .transactionDate(LocalDateTime.now())
                .build();

        int totalQuantity = 0;
        for (CreateInventoryTransactionRequest.TransactionItemRequest itemRequest : request.getItems()) {
            Equipment equipment = equipmentService.findEquipment(itemRequest.getEquipmentId());
            if (!equipment.getStorageLocation().getId().equals(location.getId())) {
                throw new IllegalArgumentException("Equipment " + equipment.getId() + " belongs to another storage location");
            }
            applyStockChange(equipment, request.getType(), itemRequest.getQuantity());
            equipmentRepository.save(equipment);

            InventoryTransactionItem item = InventoryTransactionItem.builder()
                    .transaction(transaction)
                    .equipment(equipment)
                    .quantity(itemRequest.getQuantity())
                    .notes(itemRequest.getNotes())
                    .build();
            transaction.getItems().add(item);
            totalQuantity += itemRequest.getQuantity();
        }

        transaction.setTotalQuantity(totalQuantity);
        return toResponse(transactionRepository.save(transaction));
    }

    public Page<InventoryTransactionResponse> getMyTransactions(String email, Pageable pageable) {
        return transactionRepository.findByUserEmail(email, pageable).map(this::toResponse);
    }

    public InventoryTransactionResponse getMyTransaction(String email, Long id) {
        return transactionRepository.findByUserEmailAndId(email, id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }

    public List<InventoryTransactionResponse> getByStatus(InventoryTransaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    public Page<InventoryTransactionResponse> getByLocation(Long storageLocationId, Pageable pageable) {
        return transactionRepository.findByStorageLocationId(storageLocationId, pageable).map(this::toResponse);
    }

    public List<InventoryTransactionResponse> getByPeriod(LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findByTransactionDateBetween(from, to).stream().map(this::toResponse).toList();
    }

    @Transactional
    public InventoryTransactionResponse updateStatus(Long id, InventoryTransaction.TransactionStatus status) {
        InventoryTransaction transaction = findTransaction(id);
        if (status == InventoryTransaction.TransactionStatus.CANCELLED) {
            return cancelTransaction(id);
        }
        transaction.setStatus(status);
        return toResponse(transaction);
    }

    @Transactional
    public InventoryTransactionResponse cancelTransaction(String email, Long id) {
        InventoryTransaction transaction = transactionRepository.findByUserEmailAndId(email, id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
        return cancel(transaction);
    }

    @Transactional
    public InventoryTransactionResponse cancelTransaction(Long id) {
        return cancel(findTransaction(id));
    }

    private InventoryTransactionResponse cancel(InventoryTransaction transaction) {
        if (transaction.getStatus() == InventoryTransaction.TransactionStatus.CANCELLED) {
            return toResponse(transaction);
        }
        if (transaction.getStatus() == InventoryTransaction.TransactionStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed transaction cannot be cancelled");
        }
        for (InventoryTransactionItem item : transaction.getItems()) {
            reverseStockChange(item.getEquipment(), transaction.getType(), item.getQuantity());
            equipmentRepository.save(item.getEquipment());
        }
        transaction.setStatus(InventoryTransaction.TransactionStatus.CANCELLED);
        return toResponse(transaction);
    }

    private InventoryTransaction findTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }

    private void applyStockChange(Equipment equipment, InventoryTransaction.TransactionType type, Integer quantity) {
        switch (type) {
            case ISSUE, MAINTENANCE -> decreaseAvailable(equipment, quantity);
            case RETURN -> increaseAvailable(equipment, quantity);
            case WRITE_OFF -> writeOff(equipment, quantity);
        }
    }

    private void reverseStockChange(Equipment equipment, InventoryTransaction.TransactionType type, Integer quantity) {
        switch (type) {
            case ISSUE, MAINTENANCE -> increaseAvailable(equipment, quantity);
            case RETURN -> decreaseAvailable(equipment, quantity);
            case WRITE_OFF -> reverseWriteOff(equipment, quantity);
        }
    }

    private void decreaseAvailable(Equipment equipment, Integer quantity) {
        if (equipment.getQuantityAvailable() < quantity) {
            throw new IllegalArgumentException("Not enough available quantity for " + equipment.getName());
        }
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - quantity);
    }

    private void increaseAvailable(Equipment equipment, Integer quantity) {
        int newAvailable = equipment.getQuantityAvailable() + quantity;
        if (newAvailable > equipment.getQuantityTotal()) {
            throw new IllegalArgumentException("Available quantity cannot exceed total quantity for " + equipment.getName());
        }
        equipment.setQuantityAvailable(newAvailable);
    }

    private void writeOff(Equipment equipment, Integer quantity) {
        decreaseAvailable(equipment, quantity);
        equipment.setQuantityTotal(equipment.getQuantityTotal() - quantity);
        if (equipment.getQuantityTotal() == 0) {
            equipment.setConditionStatus(Equipment.ConditionStatus.WRITTEN_OFF);
            equipment.setActive(false);
        }
    }

    private void reverseWriteOff(Equipment equipment, Integer quantity) {
        equipment.setQuantityTotal(equipment.getQuantityTotal() + quantity);
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() + quantity);
        equipment.setActive(true);
        if (equipment.getConditionStatus() == Equipment.ConditionStatus.WRITTEN_OFF) {
            equipment.setConditionStatus(Equipment.ConditionStatus.GOOD);
        }
    }

    private InventoryTransactionResponse toResponse(InventoryTransaction transaction) {
        return InventoryTransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .userName(transaction.getUser().getName())
                .storageLocationId(transaction.getStorageLocation().getId())
                .storageLocationName(transaction.getStorageLocation().getName())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .responsiblePerson(transaction.getResponsiblePerson())
                .totalQuantity(transaction.getTotalQuantity())
                .comment(transaction.getComment())
                .transactionDate(transaction.getTransactionDate())
                .items(transaction.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }

    private InventoryTransactionResponse.TransactionItemResponse toItemResponse(InventoryTransactionItem item) {
        return InventoryTransactionResponse.TransactionItemResponse.builder()
                .id(item.getId())
                .equipmentId(item.getEquipment().getId())
                .equipmentName(item.getEquipment().getName())
                .inventoryCode(item.getEquipment().getInventoryCode())
                .quantity(item.getQuantity())
                .notes(item.getNotes())
                .build();
    }
}
