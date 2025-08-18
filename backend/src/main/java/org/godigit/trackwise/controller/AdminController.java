package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.job.AssetPerformanceAnalysisJob;
import org.godigit.trackwise.job.NewsScannerJob;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminController {

    private final NewsScannerJob newsScannerJob;
    private final AssetPerformanceAnalysisJob assetPerformanceAnalysisJob;

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



}