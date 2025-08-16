package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.godigit.trackwise.model.CheckInOutLog;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.CheckInOutLogRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.CheckInOutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckInOutServiceImpl implements CheckInOutService {

    private final CheckInOutLogRepository checkInOutLogRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public CheckInOutLog checkoutAsset(UUID assetId, UUID employeeId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));

        if (asset.getStatus() == AssetStatus.RETIRED) {
            throw new IllegalStateException("Cannot checkout retired asset");
        }

        CheckInOutLog log = new CheckInOutLog();
        log.setAsset(asset);
        log.setEmployee(emp);
        log.setCheckOutTime(Instant.now());
        log = checkInOutLogRepository.save(log);

        asset.setAssignedTo(emp);
        asset.setStatus(AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        return log;
    }

    @Override
    public CheckInOutLog checkinAsset(UUID assetId, UUID employeeId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));

        CheckInOutLog log = new CheckInOutLog();
        log.setAsset(asset);
        log.setEmployee(emp);
        log.setCheckInTime(Instant.now());
        log = checkInOutLogRepository.save(log);

        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        return log;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutLog> historyByAsset(UUID assetId) {
        return checkInOutLogRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInOutLog> historyByEmployee(UUID employeeId) {
        return checkInOutLogRepository.findByEmployeeId(employeeId);
    }
}
