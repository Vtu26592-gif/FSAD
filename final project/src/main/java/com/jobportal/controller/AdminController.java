package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", userService.countByRole(User.Role.STUDENT));
        model.addAttribute("totalEmployers", userService.countByRole(User.Role.EMPLOYER));
        model.addAttribute("totalJobs", jobService.countAllJobs());
        model.addAttribute("activeJobs", jobService.countActiveJobs());
        model.addAttribute("totalApplications", applicationService.countAll());
        model.addAttribute("recentJobs", jobService.getAllJobs().stream().limit(5).toList());
        model.addAttribute("jobsByCategory", jobService.getJobStatsByCategory());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("employers", userService.getAllEmployers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String allJobs(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "admin/jobs";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jobService.deleteJob(id);
        redirectAttributes.addFlashAttribute("success", "Job removed.");
        return "redirect:/admin/jobs";
    }

    @GetMapping("/applications")
    public String allApplications(Model model) {
        model.addAttribute("applications", applicationService.countAll());
        return "admin/applications";
    }
}
