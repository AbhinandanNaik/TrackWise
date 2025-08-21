package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetScanRequest;
import org.godigit.trackwise.dto.CheckInOutRequest;
import org.godigit.trackwise.dto.CheckInOutResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.CheckInOutMapper;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.model.Enum.CheckInOutAction;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.CheckInOutLogRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.CheckInOutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckInOutServiceImpl implements CheckInOutService {

    private final CheckInOutLogRepository checkInOutLogRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger log = LoggerFactory.getLogger(CheckInOutServiceImpl.class);

    @Override
    public CheckInOutResponse checkoutAsset(CheckInOutRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));
        Employee emp = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + request.getEmployeeId()));

        // ADDED VALIDATION: Ensure asset is available before checking out.
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Asset is not available for checkout. Current status: " + asset.getStatus());
        }

        CheckInOutLog log = new CheckInOutLog();
        log.setAsset(asset);
        log.setEmployee(emp);
        log.setCheckOutTime(Instant.now());
        log.setAction(CheckInOutAction.CHECK_OUT);
        CheckInOutLog savedLog = checkInOutLogRepository.save(log);

        asset.setAssignedTo(emp);
        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        return CheckInOutMapper.toDTO(savedLog);
    }

    @Override
    public CheckInOutResponse checkinAsset(CheckInOutRequest request) {
        // CORRECTED LOGIC: Find the existing checkout log and update it.
        CheckInOutLog logToUpdate = checkInOutLogRepository
                .findFirstByAssetIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(request.getAssetId())
                .orElseThrow(() -> new IllegalStateException("Asset not found or was not checked out."));

        // Validate that the correct employee is checking it in (optional but good practice)
        if (!logToUpdate.getEmployee().getId().equals(request.getEmployeeId())) {
            throw new IllegalStateException("Asset was checked out by a different employee.");
        }

        logToUpdate.setCheckInTime(Instant.now());
        CheckInOutLog savedLog = checkInOutLogRepository.save(logToUpdate);

        Asset asset = savedLog.getAsset();
        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        return CheckInOutMapper.toDTO(savedLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutResponse> historyByAsset(UUID assetId) {
        return checkInOutLogRepository.findByAssetId(assetId)
                .stream()
                .map(CheckInOutMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutResponse> historyByEmployee(UUID employeeId) {
        return checkInOutLogRepository.findByEmployeeId(employeeId)
                .stream()
                .map(CheckInOutMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public CheckInOutResponse processAssetScan(AssetScanRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

        CheckInOutRequest actionRequest = new CheckInOutRequest();
        actionRequest.setAssetId(request.getAssetId());
        actionRequest.setEmployeeId(request.getEmployeeId());

        // The "smart" logic to decide what to do
        if (asset.getStatus() == AssetStatus.AVAILABLE) {
            log.info("Asset {} is available. Processing checkout for employee {}", asset.getName(), request.getEmployeeId());
            return this.checkoutAsset(actionRequest);
        } else if (asset.getStatus() == AssetStatus.ASSIGNED) {
            log.info("Asset {} is assigned. Processing check-in for employee {}", asset.getName(), request.getEmployeeId());
            return this.checkinAsset(actionRequest);
        } else {
            // If asset is UNDER_MAINTENANCE, RETIRED, etc.
            throw new IllegalStateException("Asset cannot be checked in or out. Current status: " + asset.getStatus());
        }
    }

}