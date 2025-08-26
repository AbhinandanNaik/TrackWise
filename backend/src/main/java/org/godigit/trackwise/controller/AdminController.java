package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.job.AssetPerformanceAnalysisJob;
import org.godigit.trackwise.job.NewsScannerJob; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for administrative tasks, such as manually triggering
 * scheduled background jobs. All endpoints in this controller are
 * protected and require an ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "1. Admin - Job Management", description = "Endpoints for manually triggering background jobs.")
public class AdminController {

    // The job beans are injected directly to be executed.
    private final NewsScannerJob newsScannerJob;
    private final AssetPerformanceAnalysisJob assetPerformanceAnalysisJob;

    /**
     * Manually triggers the News Scanner job. This is useful for testing
     * or forcing an immediate scan for recent news.
     * @return A confirmation message.
     */
    @PostMapping("/run-news-scan")
    @Operation(summary = "Manually trigger the news scanner job")
    public ResponseEntity<String> runNewsScanNow() {
        // Directly execute the job's logic.
        // Passing null is acceptable here as the context is not used in a manual run.
        newsScannerJob.executeInternal(null);
        return ResponseEntity.ok("News scanner job triggered manually.");
    }

    /**
     * Manually triggers the Asset Performance Analysis job. This is useful for
     * forcing an immediate re-analysis of all assets.
     * @return A confirmation message.
     */
    @PostMapping("/run-performance-analysis")
    @Operation(summary = "Manually trigger the asset performance analysis job")
    public ResponseEntity<String> runPerformanceAnalysisNow() {
        // Directly execute the job's logic.
        assetPerformanceAnalysisJob.executeInternal(null);
        return ResponseEntity.ok("Asset performance analysis job triggered manually.");
    }
}
