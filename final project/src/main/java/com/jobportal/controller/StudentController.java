package com.jobportal.controller;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("student", student);
        model.addAttribute("totalApplications", applicationService.getApplicationsByStudent(student).size());
        model.addAttribute("shortlisted", applicationService.countShortlistedByStudent(student.getId()));
        model.addAttribute("recentJobs", jobService.getAllActiveJobs().stream().limit(4).toList());
        model.addAttribute("myApplications", applicationService.getApplicationsByStudent(student).stream().limit(5).toList());
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("student", getCurrentUser(auth));
        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth,
                                @ModelAttribute User updatedUser,
                                @RequestParam("resumeFile") MultipartFile resumeFile,
                                RedirectAttributes redirectAttributes) throws IOException {
        User student = getCurrentUser(auth);
        student.setFullName(updatedUser.getFullName());
        student.setPhone(updatedUser.getPhone());
        student.setLocation(updatedUser.getLocation());
        student.setSkills(updatedUser.getSkills());
        student.setExperience(updatedUser.getExperience());
        student.setEducation(updatedUser.getEducation());

        if (!resumeFile.isEmpty()) {
            String fileName = userService.uploadResume(resumeFile, uploadDir);
            student.setResumeFileName(resumeFile.getOriginalFilename());
            student.setResumeFilePath(uploadDir + "/" + fileName);
        }

        userService.save(student);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @GetMapping("/jobs")
    public String browseJobs(Authentication auth,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String experience,
                             Model model) {
        User student = getCurrentUser(auth);
        var jobs = (keyword != null && !keyword.isBlank())
            ? jobService.searchJobs(keyword)
            : jobService.filterJobs(category, location, experience);

        model.addAttribute("jobs", jobs);
        model.addAttribute("student", student);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("location", location);
        model.addAttribute("experience", experience);
        return "student/browse-jobs";
    }

    @GetMapping("/jobs/{id}/apply")
    public String showApplyForm(@PathVariable Long id, Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationService.hasApplied(student, job)) {
            model.addAttribute("error", "You have already applied for this job.");
        }
        model.addAttribute("job", job);
        model.addAttribute("student", student);
        return "student/apply";
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable Long id,
                              Authentication auth,
                              @RequestParam(required = false) String coverLetter,
                              RedirectAttributes redirectAttributes) {
        User student = getCurrentUser(auth);
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        try {
            applicationService.apply(student, job, coverLetter);
            redirectAttributes.addFlashAttribute("success", "Applied successfully for: " + job.getTitle());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/applications";
    }

    @GetMapping("/applications")
    public String myApplications(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("applications", applicationService.getApplicationsByStudent(student));
        model.addAttribute("student", student);
        return "student/applications";
    }
}
