package me.gaurav.meetextension.dto;


public class SummaryRequest {
    private String title;
    private String rawTranscript;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRawTranscript() { return rawTranscript; }
    public void setRawTranscript(String rawTranscript) { this.rawTranscript = rawTranscript; }
}