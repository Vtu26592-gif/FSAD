package com.jobportal.controller;

import com.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobBrowseController {

    @Autowired
    private JobService jobService;

    // Public job listing
    @GetMapping("/jobs")
    public String browseJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String experience,
            Model model) {

        var jobs = (keyword != null && !keyword.isBlank())
            ? jobService.searchJobs(keyword)
            : jobService.filterJobs(category, location, experience);

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("location", location);
        model.addAttribute("experience", experience);
        return "jobs";
    }

    // View single job detail (public)
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        var job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        model.addAttribute("job", job);
        return "job-detail";
    }
}
