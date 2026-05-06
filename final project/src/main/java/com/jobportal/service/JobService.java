package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByStatusOrderByPostedAtDesc(Job.JobStatus.ACTIVE);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    public List<Job> searchJobs(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllActiveJobs();
        return jobRepository.searchByKeyword(keyword);
    }

    public List<Job> filterJobs(String category, String location, String experience) {
        boolean allNull = (category == null || category.isBlank())
                       && (location == null || location.isBlank())
                       && (experience == null || experience.isBlank());
        if (allNull) return getAllActiveJobs();

        return jobRepository.filterJobs(
            (category != null && !category.isBlank()) ? category : null,
            (location != null && !location.isBlank()) ? location : null,
            (experience != null && !experience.isBlank()) ? experience : null
        );
    }

    public List<Object[]> getJobStatsByCategory() {
        return jobRepository.countActiveJobsByCategory();
    }

    public long countAllJobs() {
        return jobRepository.count();
    }

    public long countActiveJobs() {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE).size();
    }
}
