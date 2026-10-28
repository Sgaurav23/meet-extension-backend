package me.gaurav.meetextension.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import me.gaurav.meetextension.model.Summary;
import me.gaurav.meetextension.repository.SummaryRepository;

@Service 
public class SummaryService {
    private final SummaryRepository summaryRepository;

    public SummaryService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository; 
    }
    
    public List<Summary> getAllSummary() {
        return summaryRepository.findAll();
    }

    public Optional<Summary> getSummaryById(Long id) {
        return summaryRepository.findById(id);
    }

    public Optional<Summary> updateSummary(Summary summary) {
        Summary existingSummary = summaryRepository.findById(summary.getId()).orElse(null);

        if(existingSummary != null) {
            existingSummary.setTitle(summary.getTitle());
            existingSummary.setSummary(summary.getSummary());
            summaryRepository.save(existingSummary);
            return Optional.of(existingSummary);
        }
        return Optional.empty();

    }

    public Summary createSummary(Summary summary) {
        return summaryRepository.save(summary);
    }

    public void removeSummary() {
        summaryRepository.deleteAll();
    }
}
