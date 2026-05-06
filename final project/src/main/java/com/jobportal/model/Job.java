package com.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Job title is required")
    @Size(min = 3, max = 100)
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 20)
    @Column(length = 3000)
    private String description;

    @NotNull(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Location is required")
    private String location;

    // e.g. IT, Finance, Marketing, Healthcare
    @NotNull(message = "Category is required")
    private String category;

    // e.g. Full-time, Part-time, Internship, Remote
    private String jobType;

    private String salaryRange;

    // e.g. Fresher, 1-2 years, 3-5 years
    private String experienceRequired;

    @NotNull
    private String skillsRequired;

    private LocalDate lastDateToApply;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.ACTIVE;

    private LocalDateTime postedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;

    public enum JobStatus {
        ACTIVE, CLOSED, DRAFT
    }
}
