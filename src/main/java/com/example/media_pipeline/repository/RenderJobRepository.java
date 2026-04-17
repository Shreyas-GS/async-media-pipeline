package com.example.media_pipeline.repository;

import com.example.media_pipeline.entity.RenderJob;
import com.example.media_pipeline.entity.RenderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RenderJobRepository extends JpaRepository<RenderJob, UUID> {

    // Spring automatically writes the SQL for this based purely on the method name!
    List<RenderJob> findByStatus(RenderStatus status);
}