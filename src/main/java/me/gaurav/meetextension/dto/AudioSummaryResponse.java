package me.gaurav.meetextension.dto;

public class AudioSummaryResponse {

    private final String transcript;
    private final String summary;

    public AudioSummaryResponse(String transcript, String summary) {
        this.transcript = transcript;
        this.summary = summary;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getSummary() {
        return summary;
    }
}
