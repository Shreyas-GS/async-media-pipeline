package com.example.media_pipeline.repository;

import com.example.media_pipeline.entity.JobMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobMetadataRepository extends JpaRepository<JobMetadata, Long> {
}