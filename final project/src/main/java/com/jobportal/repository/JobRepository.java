package com.jobportal.repository;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(Job.JobStatus status);

    List<Job> findByEmployer(User employer);

    List<Job> findByEmployerAndStatus(User employer, Job.JobStatus status);

    List<Job> findByCategoryIgnoreCase(String category);

    List<Job> findByLocationContainingIgnoreCase(String location);

    // Search across multiple fields
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND (" +
           "LOWER(j.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

    // Filter: category + location + experience
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(:category IS NULL OR LOWER(j.category) = LOWER(:category)) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%',:location,'%'))) AND " +
           "(:experience IS NULL OR LOWER(j.experienceRequired) = LOWER(:experience))")
    List<Job> filterJobs(
        @Param("category") String category,
        @Param("location") String location,
        @Param("experience") String experience
    );

    // Count active jobs by category
    @Query("SELECT j.category, COUNT(j) FROM Job j WHERE j.status = 'ACTIVE' GROUP BY j.category")
    List<Object[]> countActiveJobsByCategory();

    List<Job> findByStatusOrderByPostedAtDesc(Job.JobStatus status);
}
