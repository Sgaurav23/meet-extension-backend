package me.gaurav.meetextension;

import me.gaurav.meetextension.dto.AudioSummaryResponse;
import me.gaurav.meetextension.dto.SummaryRequest;
import me.gaurav.meetextension.model.Summary;
import me.gaurav.meetextension.service.MeetingSummarizerService;
import me.gaurav.meetextension.service.SummaryService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/summarize")
@CrossOrigin(origins = "*") // Allows requests from Chrome Extension
public class SummaryController {

    private final MeetingSummarizerService summarizerService;
    private final SummaryService summaryService;

    public SummaryController(MeetingSummarizerService summarizerService, SummaryService summaryService) { 
        this.summarizerService = summarizerService;
        this.summaryService = summaryService;
    }

    @PostMapping
    public ResponseEntity<String> generateSummary(@RequestBody SummaryRequest request) {
        if (request.getRawTranscript() == null || request.getRawTranscript().isBlank()) {
            return ResponseEntity.badRequest().body("Transcript cannot be empty.");
        }
        
        String summary = summarizerService.summarizeMeeting(
            request.getTitle() != null ? request.getTitle() : "Google Meet", 
            request.getRawTranscript()
        );

        Summary newSummary = new Summary();

        newSummary.setTitle(request.getTitle() != null ? request.getTitle() : "Google Meet");
        newSummary.setSummary(summary);
        summaryService.createSummary(newSummary);
        
        return ResponseEntity.ok(summary);
    }

    @PostMapping(path = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioSummaryResponse> generateSummaryFromAudio(
        @RequestParam(defaultValue = "Google Meet") String title,
        @RequestParam("audio") MultipartFile audio
    ) throws IOException {
        if (audio.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (audio.getSize() > 14L * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }
        String contentType = audio.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("audio/")) {
            return ResponseEntity.badRequest().build();
        }

        String meetingTitle = title.isBlank() ? "Google Meet" : title;
        String transcript = summarizerService.transcribeMeetingAudio(
            meetingTitle,
            audio.getBytes(),
            contentType
        );
        String summary = summarizerService.summarizeMeeting(meetingTitle, transcript);

        Summary newSummary = new Summary();
        newSummary.setTitle(meetingTitle);
        newSummary.setSummary(summary);
        summaryService.createSummary(newSummary);

        return ResponseEntity.ok(new AudioSummaryResponse(transcript, summary));
    }

    @GetMapping 
    public ResponseEntity<List<Summary>> getSummary() {
        return ResponseEntity.ok(summaryService.getAllSummary());
    }

    @PutMapping
    public ResponseEntity<Optional<Summary>> updateSumary(@RequestBody Summary summary) {
        return ResponseEntity.ok(summaryService.updateSummary(summary));
    }

    @DeleteMapping 
    public ResponseEntity<Void> deleteSummary() {
        return ResponseEntity.noContent().build();
    }
}