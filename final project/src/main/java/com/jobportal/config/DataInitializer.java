package com.jobportal.config;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepo,
                                      JobRepository jobRepo,
                                      PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() > 0) return;

            // Admin
            User admin = userRepo.save(User.builder()
                .fullName("Admin User")
                .email("admin@jobportal.com")
                .password(encoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build());

            // Employer 1
            User emp1 = userRepo.save(User.builder()
                .fullName("Rahul Sharma")
                .email("employer@techcorp.com")
                .password(encoder.encode("emp123"))
                .role(User.Role.EMPLOYER)
                .companyName("TechCorp India Pvt Ltd")
                .companyDescription("Leading IT solutions company")
                .industry("Information Technology")
                .website("https://techcorp.in")
                .location("Bangalore")
                .build());

            // Employer 2
            User emp2 = userRepo.save(User.builder()
                .fullName("Priya Nair")
                .email("hr@innovate.com")
                .password(encoder.encode("emp123"))
                .role(User.Role.EMPLOYER)
                .companyName("Innovate Solutions")
                .companyDescription("Digital transformation consulting firm")
                .industry("Consulting")
                .website("https://innovate.com")
                .location("Chennai")
                .build());

            // Student
            userRepo.save(User.builder()
                .fullName("Arjun Kumar")
                .email("student@gmail.com")
                .password(encoder.encode("student123"))
                .role(User.Role.STUDENT)
                .phone("9876543210")
                .location("Hyderabad")
                .skills("Java, Spring Boot, MySQL, React")
                .experience("Fresher")
                .education("B.Tech CSE, 2024")
                .build());

            // Jobs
            jobRepo.save(Job.builder()
                .title("Java Backend Developer")
                .description("We are looking for a Java backend developer with Spring Boot experience. You will work on enterprise-grade REST APIs, microservices architecture, and database design. Excellent communication skills required.")
                .companyName("TechCorp India Pvt Ltd")
                .location("Bangalore")
                .category("IT")
                .jobType("Full-time")
                .salaryRange("₹4L - ₹8L per annum")
                .experienceRequired("Fresher")
                .skillsRequired("Java, Spring Boot, MySQL, REST API")
                .lastDateToApply(LocalDate.now().plusDays(30))
                .status(Job.JobStatus.ACTIVE)
                .employer(emp1)
                .build());

            jobRepo.save(Job.builder()
                .title("React Frontend Developer")
                .description("Join our UI team to build stunning web applications using React.js. You will collaborate with designers and backend teams to create seamless user experiences. Knowledge of TypeScript is a plus.")
                .companyName("TechCorp India Pvt Ltd")
                .location("Remote")
                .category("IT")
                .jobType("Remote")
                .salaryRange("₹3L - ₹6L per annum")
                .experienceRequired("1-2 years")
                .skillsRequired("React, JavaScript, CSS, HTML5")
                .lastDateToApply(LocalDate.now().plusDays(20))
                .status(Job.JobStatus.ACTIVE)
                .employer(emp1)
                .build());

            jobRepo.save(Job.builder()
                .title("Business Analyst Intern")
                .description("6-month internship for final year students. Work on client requirements gathering, process mapping, and data analysis. Great opportunity to learn consulting skills in a fast-paced environment.")
                .companyName("Innovate Solutions")
                .location("Chennai")
                .category("Finance")
                .jobType("Internship")
                .salaryRange("₹15,000/month stipend")
                .experienceRequired("Fresher")
                .skillsRequired("Excel, PowerPoint, Analytical Thinking, SQL")
                .lastDateToApply(LocalDate.now().plusDays(15))
                .status(Job.JobStatus.ACTIVE)
                .employer(emp2)
                .build());

            jobRepo.save(Job.builder()
                .title("Data Science Engineer")
                .description("Looking for a data scientist to build ML models, perform statistical analysis, and generate business insights. Experience with Python, Pandas, and Scikit-learn required.")
                .companyName("Innovate Solutions")
                .location("Hyderabad")
                .category("IT")
                .jobType("Full-time")
                .salaryRange("₹6L - ₹12L per annum")
                .experienceRequired("1-2 years")
                .skillsRequired("Python, ML, Pandas, TensorFlow, SQL")
                .lastDateToApply(LocalDate.now().plusDays(25))
                .status(Job.JobStatus.ACTIVE)
                .employer(emp2)
                .build());

            jobRepo.save(Job.builder()
                .title("HR Recruiter")
                .description("We need a dynamic HR recruiter to manage end-to-end recruitment. Duties include sourcing candidates, conducting interviews, and onboarding. Strong communication and networking skills required.")
                .companyName("TechCorp India Pvt Ltd")
                .location("Mumbai")
                .category("HR")
                .jobType("Full-time")
                .salaryRange("₹3L - ₹5L per annum")
                .experienceRequired("Fresher")
                .skillsRequired("Communication, Excel, HR Software, Networking")
                .lastDateToApply(LocalDate.now().plusDays(18))
                .status(Job.JobStatus.ACTIVE)
                .employer(emp1)
                .build());

            System.out.println("✅ Sample data seeded successfully.");
            System.out.println("   Admin:    admin@jobportal.com / admin123");
            System.out.println("   Employer: employer@techcorp.com / emp123");
            System.out.println("   Student:  student@gmail.com / student123");
        };
    }
}
