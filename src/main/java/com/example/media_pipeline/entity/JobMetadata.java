package com.example.media_pipeline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private RenderJob renderJob;

    @Column(name = "scene_description", nullable = false, columnDefinition = "TEXT")
    private String sceneDescription;

    @Column(name = "characters_involved")
    private String charactersInvolved;

    @Column(name = "reference_image_url", length = 512)
    private String referenceImageUrl;

    @Column(name = "preserve_consistency")
    private Boolean preserveConsistency;

    @Column(length = 20)
    private String resolution;
}