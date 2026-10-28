package me.gaurav.meetextension.service;

import java.util.Objects;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

@Service
public class MeetingSummarizerService {

    private final ChatModel chatModel;

    public MeetingSummarizerService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String transcribeMeetingAudio(String title, byte[] audio, String contentType) {
        String prompt = """
            Transcribe the attached Google Meet recording accurately.
            Preserve each speaker's original language; do not translate or summarize the transcript.
            Label speakers only when they can be distinguished from the audio, and do not invent words.
            Return only the transcript.

            Meeting Title: %s

            Provide a structured response formatted in Markdown:
            1. Executive Summary (2-3 sentences)
            2. Key Decisions Made
            3. Action Items & Assignees
            """.formatted(title);
        Media audioMedia = new Media(
            MimeType.valueOf(contentType),
            new ByteArrayResource(audio)
        );
        UserMessage message = UserMessage.builder()
            .text(prompt)
            .media(audioMedia)
            .build();

        String transcript = chatModel.call(new Prompt(message))
            .getResult()
            .getOutput()
            .getText();
        if (transcript == null || transcript.isBlank()) {
            throw new IllegalStateException("The audio transcription service returned an empty transcript.");
        }
        return transcript.trim();
    }

    public String summarizeMeeting(String title, String rawTranscript) {
        String prompt = """
            You are an executive assistant summarizing a Google Meet recording.
            Meeting Title: %s
            
            Transcript:
            %s
            
            The transcript may be in any language. Understand it in its original language and write the recap in English.
            Do not invent decisions, names, or action items when they are not stated in the transcript.

            Provide a structured response formatted in Markdown:
            1. Executive Summary (2-3 sentences)
            2. Key Decisions Made
            3. Action Items & Assignees
            """.formatted(title, rawTranscript);

        return Objects.requireNonNull(chatModel.call(prompt), "The summary service returned no response.");
    }
}