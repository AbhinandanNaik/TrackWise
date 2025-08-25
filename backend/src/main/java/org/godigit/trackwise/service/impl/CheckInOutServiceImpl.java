package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetScanRequest;
import org.godigit.trackwise.dto.CheckInOutRequest;
import org.godigit.trackwise.dto.CheckInOutResponse;
import org.godigit.trackwise.exception.InvalidStateException;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.CheckInOutMapper;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.model.Enum.*;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.CheckInOutLogRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.CheckInOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for handling all business logic related to
 * asset check-ins and check-outs.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields
@Transactional // Ensures all public methods run inside a database transaction
public class CheckInOutServiceImpl implements CheckInOutService {

    // Dependencies injected by the constructor
    private final CheckInOutLogRepository checkInOutLogRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger log = LoggerFactory.getLogger(CheckInOutServiceImpl.class);

    /**
     * Checks out an asset to an employee.
     */
    @Override
    public CheckInOutResponse checkoutAsset(CheckInOutRequest request) {
        // Find the asset by its ID, or throw a specific error if it doesn't exist.
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

        // Find the employee by their ID, or throw an error if not found.
        Employee emp = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + request.getEmployeeId()));

        // Business Rule: Ensure the asset is actually available before checking it out.
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new InvalidStateException("Asset is not available for checkout. Current status: " + asset.getStatus());
        }

        // Create a new log entry for this transaction.
        CheckInOutLog log = new CheckInOutLog();
        log.setAsset(asset);
        log.setEmployee(emp);
        log.setCheckOutTime(Instant.now()); // Set the checkout time to now.
        log.setAction(CheckInOutAction.CHECK_OUT); // Record that this was a CHECK_OUT action.

        // Save the new log entry to the database.
        CheckInOutLog savedLog = checkInOutLogRepository.save(log);

        // Update the asset's state: assign it to the employee and change its status.
        asset.setAssignedTo(emp);
        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        // Convert the saved database entity to a DTO to safely return it to the client.
        return CheckInOutMapper.toDTO(savedLog);
    }

    /**
     * Checks in a previously checked-out asset.
     */
    @Override
    public CheckInOutResponse checkinAsset(CheckInOutRequest request) {
        // Find the most recent, open check-out log for this asset.
        CheckInOutLog logToUpdate = checkInOutLogRepository
                .findFirstByAssetIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(request.getAssetId())
                .orElseThrow(() -> new IllegalStateException("Asset not found or was not checked out."));

        // Business Rule: Ensure the person checking in is the one who checked it out.
        if (!logToUpdate.getEmployee().getId().equals(request.getEmployeeId())) {
            throw new IllegalStateException("Asset can only be checked in by the employee who checked it out.");
        }

        // Update the existing log entry by setting the check-in time.
        logToUpdate.setCheckInTime(Instant.now());

        // Save the updated log entry.
        CheckInOutLog savedLog = checkInOutLogRepository.save(logToUpdate);

        // Get the asset associated with this log.
        Asset asset = savedLog.getAsset();

        // Update the asset's state: remove the employee assignment and make it available.
        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        // Convert the updated entity to a DTO for the response.
        return CheckInOutMapper.toDTO(savedLog);
    }

    /**
     * Retrieves the complete check-in/out history for a specific asset.
     */
    @Override
    @Transactional(readOnly = true) // This is a read-only operation, a performance optimization.
    public List<CheckInOutResponse> historyByAsset(UUID assetId) {
        // Fetch all log entries for the given assetId from the database.
        return checkInOutLogRepository.findByAssetId(assetId)
                .stream() // Convert the list to a stream for processing.
                .map(CheckInOutMapper::toDTO) // Map each log entity to its DTO representation.
                .collect(Collectors.toList()); // Collect the results back into a list.
    }

    /**
     * "Smart" action that automatically performs a check-in or check-out.
     */
    @Override
    public CheckInOutResponse processAssetScan(AssetScanRequest request) {
        // Find the asset that was scanned.
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

        // Create a standard request object to pass to the other methods.
        CheckInOutRequest actionRequest = new CheckInOutRequest();
        actionRequest.setAssetId(request.getAssetId());
        actionRequest.setEmployeeId(request.getEmployeeId());

        // Core Logic: Decide which action to take based on the asset's current status.
        if (asset.getStatus() == AssetStatus.AVAILABLE) {
            // If it's available, it needs to be checked out.
            log.info("Asset '{}' is available. Processing checkout for employee {}", asset.getName(), request.getEmployeeId());
            return this.checkoutAsset(actionRequest);
        } else if (asset.getStatus() == AssetStatus.ASSIGNED) {
            // If it's already assigned, it needs to be checked in.
            log.info("Asset '{}' is assigned. Processing check-in for employee {}", asset.getName(), request.getEmployeeId());
            return this.checkinAsset(actionRequest);
        } else {
            // If it's in any other state (e.g., retired), throw an error.
            throw new IllegalStateException("Asset cannot be checked in or out. Current status: " + asset.getStatus());
        }
    }

    /**
     * Finds all assets that are overdue.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutResponse> findOverdueAssets(LocalDate since) {
        // Convert the LocalDate to an Instant to match the database column type.
        Instant sinceInstant = since.atStartOfDay(ZoneId.systemDefault()).toInstant();

        // Call the repository method to find logs checked out before the 'since' date
        // and where the check-in time is still null.
        return checkInOutLogRepository.findByCheckOutTimeBeforeAndCheckInTimeIsNull(sinceInstant)
                .stream()
                .map(CheckInOutMapper::toDTO) // Convert each found log to a DTO.
                .collect(Collectors.toList());
    }

    /**
     * Finds the current open check-out log for a specific employee.
     */
    @Override
    @Transactional(readOnly = true)
    public CheckInOutResponse findCurrentCheckoutByEmployee(UUID employeeId) {
        // Call the repository method to find the most recent log for an employee
        // where the check-in time is null.
        return checkInOutLogRepository.findFirstByEmployeeIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(employeeId)
                .map(CheckInOutMapper::toDTO) // If a log is found, map it to a DTO.
                .orElse(null); // If no log is found, return null.
    }

    /**
     * Retrieves the complete check-in/out history for a specific employee.
     *
     * @param employeeId The UUID of the employee.
     * @return A list of log DTOs for the employee.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutResponse> historyByEmployee(UUID employeeId) {
        return checkInOutLogRepository.findByEmployeeId(employeeId)
                .stream()
                .map(CheckInOutMapper::toDTO)
                .collect(Collectors.toList());
    }
}