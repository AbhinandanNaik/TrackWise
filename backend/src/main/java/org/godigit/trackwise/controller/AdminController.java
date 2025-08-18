package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
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

    // This endpoint manually triggers the job's logic
    @PostMapping("/run-news-scan")
    public ResponseEntity<String> runNewsScanNow() {
        newsScannerJob.executeInternal(null); // The context can be null for a manual run
        return ResponseEntity.ok("News scanner job triggered manually.");
    }
}