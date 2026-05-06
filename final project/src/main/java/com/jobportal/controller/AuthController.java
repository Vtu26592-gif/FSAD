package com.jobportal.controller;

import com.jobportal.model.User;
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
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    // Route to correct dashboard based on role
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")))
            return "redirect:/student/dashboard";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYER")))
            return "redirect:/employer/dashboard";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return "redirect:/admin/dashboard";
        return "redirect:/login";
    }

    // Show register role selection
    @GetMapping("/register")
    public String registerChoice() {
        return "register-choice";
    }

    // Student registration
    @GetMapping("/register/student")
    public String studentRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register-student";
    }

    @PostMapping("/register/student")
    public String registerStudent(@Valid @ModelAttribute("user") User user,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "register-student";
        try {
            user.setRole(User.Role.STUDENT);
            userService.register(user);
            redirectAttributes.addFlashAttribute("success", "Registered successfully! Please login.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            return "register-student";
        }
    }

    // Employer registration
    @GetMapping("/register/employer")
    public String employerRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register-employer";
    }

    @PostMapping("/register/employer")
    public String registerEmployer(@Valid @ModelAttribute("user") User user,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "register-employer";
        try {
            user.setRole(User.Role.EMPLOYER);
            userService.register(user);
            redirectAttributes.addFlashAttribute("success", "Employer account created! Please login.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            return "register-employer";
        }
    }
}
