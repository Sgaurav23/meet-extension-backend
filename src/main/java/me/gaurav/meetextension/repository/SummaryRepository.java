package me.gaurav.meetextension.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.gaurav.meetextension.model.Summary;

@Repository 
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    
}
