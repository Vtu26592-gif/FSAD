package com.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // STUDENT, EMPLOYER, ADMIN

    // Student-specific fields
    private String phone;
    private String location;
    private String skills;
    private String experience; // e.g. "Fresher", "1-2 years"
    private String education;
    private String resumeFileName;
    private String resumeFilePath;

    // Employer-specific fields
    private String companyName;
    private String companyDescription;
    private String industry;
    private String website;

    private boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<Job> postedJobs;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<Application> applications;

    public enum Role {
        STUDENT, EMPLOYER, ADMIN
    }
}
