package com.examease;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Seed {

    @Bean
    CommandLineRunner initializeData(
            UserRepository users,
            ExamRepository exams,
            PasswordEncoder encoder) {

        return args -> {
            if (!users.existsByUsername("admin")) {
                users.save(new AppUser(
                        "admin", encoder.encode("Admin@123"), "ADMIN"));
            }

            if (exams.count() == 0) {
                ExamEntity javaExam = new ExamEntity(
                        "Java Fundamentals",
                        "Core Java, OOP and collections", 3);

                javaExam.questions.add(new QuestionEntity(
                        javaExam, "Which keyword creates a subclass?",
                        "this", "extends", "super", "implements", 1));

                javaExam.questions.add(new QuestionEntity(
                        javaExam, "Which collection stores unique elements?",
                        "List", "ArrayList", "Set", "Queue", 2));

                javaExam.questions.add(new QuestionEntity(
                        javaExam, "Which method is the Java program entry point?",
                        "start()", "main()", "run()", "init()", 1));

                exams.save(javaExam);

                ExamEntity sqlExam = new ExamEntity(
                        "SQL Basics", "Queries, filtering and databases", 3);

                sqlExam.questions.add(new QuestionEntity(
                        sqlExam, "Which SQL command retrieves data?",
                        "GET", "SELECT", "PUSH", "FETCH ALL", 1));

                sqlExam.questions.add(new QuestionEntity(
                        sqlExam, "Which clause filters rows?",
                        "ORDER BY", "GROUP BY", "WHERE", "JOIN", 2));

                sqlExam.questions.add(new QuestionEntity(
                        sqlExam, "Which key uniquely identifies a row?",
                        "Foreign key", "Primary key", "Index only", "View", 1));

                exams.save(sqlExam);
            }
        };
    }
}