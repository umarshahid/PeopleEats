package com.peopleeats.web.feedback;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackRepository repository;

    public FeedbackController(FeedbackRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackRequest request) {
        String riderName = request.getRiderName().trim();
        String comment = request.getComment() == null ? "" : request.getComment().trim();

        if (request.getOrderNo() <= 0 || riderName.isBlank() || request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest()
                    .body(new FeedbackResponse(false, "Invalid feedback data."));
        }

        int inserted = repository.insert(request.getOrderNo(), riderName, request.getRating(), comment);
        if (inserted > 0) {
            return ResponseEntity.ok(new FeedbackResponse(true, "Feedback submitted."));
        }
        return ResponseEntity.internalServerError()
                .body(new FeedbackResponse(false, "Failed to submit feedback."));
    }

    @GetMapping("/rider")
    public ResponseEntity<RiderRatingResponse> riderRating(@RequestParam String name) {
        String riderName = name == null ? "" : name.trim();
        if (riderName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Double avg = repository.getAverageRating(riderName);
        Integer total = repository.getTotalRatings(riderName);
        double average = avg == null ? 0.0 : avg;
        int count = total == null ? 0 : total;
        return ResponseEntity.ok(new RiderRatingResponse(riderName, average, count));
    }

    @GetMapping("/rider/history")
    public ResponseEntity<java.util.List<FeedbackEntry>> riderHistory(@RequestParam String name,
                                                                       @RequestParam(defaultValue = "10") int limit) {
        String riderName = name == null ? "" : name.trim();
        if (riderName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        int capped = Math.min(Math.max(limit, 1), 50);
        return ResponseEntity.ok(repository.getRecentFeedback(riderName, capped));
    }
}
