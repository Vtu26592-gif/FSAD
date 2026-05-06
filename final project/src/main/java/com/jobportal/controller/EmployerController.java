package com.jobportal.controller;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User employer = getCurrentUser(auth);
        var myJobs = jobService.getJobsByEmployer(employer);
        model.addAttribute("employer", employer);
        model.addAttribute("totalJobs", myJobs.size());
        model.addAttribute("totalApplications", applicationService.countByEmployer(employer.getId()));
        model.addAttribute("shortlisted", applicationService.countShortlistedByEmployer(employer.getId()));
        model.addAttribute("recentJobs", myJobs.stream().limit(5).toList());
        return "employer/dashboard";
    }

    @GetMapping("/jobs")
    public String myJobs(Authentication auth, Model model) {
        User employer = getCurrentUser(auth);
        model.addAttribute("jobs", jobService.getJobsByEmployer(employer));
        model.addAttribute("employer", employer);
        return "employer/jobs";
    }

    @GetMapping("/jobs/add")
    public String addJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/add-job";
    }

    @PostMapping("/jobs/add")
    public String saveJob(Authentication auth,
                          @Valid @ModelAttribute("job") Job job,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "employer/add-job";
        User employer = getCurrentUser(auth);
        job.setEmployer(employer);
        job.setCompanyName(employer.getCompanyName() != null ? employer.getCompanyName() : job.getCompanyName());
        jobService.saveJob(job);
        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/edit/{id}")
    public String editJobForm(@PathVariable Long id, Authentication auth, Model model) {
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        User employer = getCurrentUser(auth);
        if (!job.getEmployer().getId().equals(employer.getId()))
            throw new RuntimeException("Unauthorized");
        model.addAttribute("job", job);
        return "employer/edit-job";
    }

    @PostMapping("/jobs/edit/{id}")
    public String updateJob(@PathVariable Long id,
                            Authentication auth,
                            @Valid @ModelAttribute("job") Job job,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "employer/edit-job";
        User employer = getCurrentUser(auth);
        job.setId(id);
        job.setEmployer(employer);
        jobService.saveJob(job);
        redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id, Authentication auth,
                            RedirectAttributes redirectAttributes) {
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        User employer = getCurrentUser(auth);
        if (!job.getEmployer().getId().equals(employer.getId()))
            throw new RuntimeException("Unauthorized");
        jobService.deleteJob(id);
        redirectAttributes.addFlashAttribute("success", "Job deleted.");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String viewApplicants(@PathVariable Long id, Authentication auth, Model model) {
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getApplicationsByJob(id));
        return "employer/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String remarks,
                                          RedirectAttributes redirectAttributes) {
        Application.ApplicationStatus appStatus = Application.ApplicationStatus.valueOf(status);
        Application app = applicationService.updateStatus(id, appStatus, remarks);
        redirectAttributes.addFlashAttribute("success", "Application status updated to: " + status);
        return "redirect:/employer/jobs/" + app.getJob().getId() + "/applicants";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("employer", getCurrentUser(auth));
        return "employer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth,
                                @ModelAttribute User updated,
                                RedirectAttributes redirectAttributes) {
        User employer = getCurrentUser(auth);
        employer.setFullName(updated.getFullName());
        employer.setPhone(updated.getPhone());
        employer.setCompanyName(updated.getCompanyName());
        employer.setCompanyDescription(updated.getCompanyDescription());
        employer.setIndustry(updated.getIndustry());
        employer.setWebsite(updated.getWebsite());
        employer.setLocation(updated.getLocation());
        userService.save(employer);
        redirectAttributes.addFlashAttribute("success", "Profile updated!");
        return "redirect:/employer/profile";
    }
}
