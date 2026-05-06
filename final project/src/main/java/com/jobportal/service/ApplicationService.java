package com.jobportal.service;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application apply(User student, Job job, String coverLetter) {
        if (applicationRepository.existsByApplicantAndJob(student, job)) {
            throw new IllegalStateException("You have already applied for this job.");
        }
        Application app = Application.builder()
            .applicant(student)
            .job(job)
            .coverLetter(coverLetter)
            .resumeFileName(student.getResumeFileName())
            .resumeFilePath(student.getResumeFilePath())
            .status(Application.ApplicationStatus.APPLIED)
            .appliedAt(LocalDateTime.now())
            .build();
        return applicationRepository.save(app);
    }

    public List<Application> getApplicationsByStudent(User student) {
        return applicationRepository.findByApplicant(student);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getApplicationsByEmployer(Long employerId) {
        return applicationRepository.findByEmployerId(employerId);
    }

    public Optional<Application> getById(Long id) {
        return applicationRepository.findById(id);
    }

    public Application updateStatus(Long id, Application.ApplicationStatus status, String remarks) {
        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        app.setEmployerRemarks(remarks);
        app.setUpdatedAt(LocalDateTime.now());
        return applicationRepository.save(app);
    }

    public boolean hasApplied(User student, Job job) {
        return applicationRepository.existsByApplicantAndJob(student, job);
    }

    public long countByEmployer(Long employerId) {
        return applicationRepository.countByEmployerId(employerId);
    }

    public long countShortlistedByEmployer(Long employerId) {
        return applicationRepository.countShortlistedByEmployerId(employerId);
    }

    public long countShortlistedByStudent(Long studentId) {
        return applicationRepository.countShortlistedByStudentId(studentId);
    }

    public long countAll() {
        return applicationRepository.count();
    }
}
