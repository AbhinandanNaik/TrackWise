package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.job.AssetPerformanceAnalysisJob;
import org.godigit.trackwise.job.NewsScannerJob;
import org.godigit.trackwise.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminController {

    private final NewsScannerJob newsScannerJob;
    private final AssetPerformanceAnalysisJob assetPerformanceAnalysisJob;
    private final EmployeeService employeeService;

    // This endpoint manually triggers the job's logic
    @PostMapping("/run-news-scan")
    public ResponseEntity<String> runNewsScanNow() {
        newsScannerJob.executeInternal(null);
        return ResponseEntity.ok("News scanner job triggered manually.");
    }

    // This endpoint manually triggers the asset performance analysis job
    @PostMapping("/run-performance-analysis")
    public ResponseEntity<String> runPerformanceAnalysisNow() {
        assetPerformanceAnalysisJob.executeInternal(null);
        return ResponseEntity.ok("Asset performance analysis job triggered manually.");
    }


    @PutMapping("/employees/{employeeId}/assign-department")
    public ResponseEntity<Void> assignDepartment(
            @PathVariable UUID employeeId,
            @RequestParam UUID departmentId) {

        employeeService.assignDepartment(employeeId, departmentId);
        return ResponseEntity.ok().build();
    }



}