package com.jobportal.repository;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicant(User applicant);

    List<Application> findByJob(Job job);

    List<Application> findByJobId(Long jobId);

    List<Application> findByApplicantId(Long applicantId);

    boolean existsByApplicantAndJob(User applicant, Job job);

    List<Application> findByStatus(Application.ApplicationStatus status);

    // Applications for jobs posted by a specific employer
    @Query("SELECT a FROM Application a WHERE a.job.employer.id = :employerId ORDER BY a.appliedAt DESC")
    List<Application> findByEmployerId(@Param("employerId") Long employerId);

    // Count by status for a job
    @Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.job.id = :jobId GROUP BY a.status")
    List<Object[]> countByStatusForJob(@Param("jobId") Long jobId);

    // Dashboard stats
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer.id = :employerId")
    long countByEmployerId(@Param("employerId") Long employerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer.id = :employerId AND a.status = 'SHORTLISTED'")
    long countShortlistedByEmployerId(@Param("employerId") Long employerId);

    // Student dashboard
    @Query("SELECT COUNT(a) FROM Application a WHERE a.applicant.id = :studentId AND a.status = 'SHORTLISTED'")
    long countShortlistedByStudentId(@Param("studentId") Long studentId);
}
