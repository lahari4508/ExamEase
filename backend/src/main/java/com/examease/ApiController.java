package com.examease;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserRepository users;
    private final ExamRepository exams;
    private final ResultRepository results;
    private final PasswordEncoder encoder;

    public ApiController(UserRepository users, ExamRepository exams,
                         ResultRepository results, PasswordEncoder encoder) {
        this.users = users;
        this.exams = exams;
        this.results = results;
        this.encoder = encoder;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Register x) {
        if (x.username() == null ||
                !x.username().matches("[A-Za-z0-9_.-]{3,80}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username must be at least 3 valid characters."));
        }

        if (x.password() == null || x.password().length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must be at least 8 characters."));
        }

        if (users.existsByUsername(x.username())) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Username already exists."));
        }

        users.save(new AppUser(
                x.username(), encoder.encode(x.password()), "STUDENT"));

        return ResponseEntity.status(201)
                .body(Map.of("message", "Account created. Please sign in."));
    }

    @GetMapping("/auth/login")
    public Map<String, String> login(Authentication auth) {
        String role = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "ADMIN" : "STUDENT";

        return Map.of("username", auth.getName(), "role", role);
    }

    @GetMapping("/exams")
    public List<?> allExams() {
        return exams.findAll().stream()
                .map(e -> Map.of(
                        "id", e.id,
                        "title", e.title,
                        "description", e.description,
                        "questionCount", e.questions.size()))
                .toList();
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<?> exam(@PathVariable long id) {
        return exams.findById(id)
                .<ResponseEntity<?>>map(e -> ResponseEntity.ok(dto(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/exams/{id}/submit")
    public ResponseEntity<?> submit(
            @PathVariable long id,
            @RequestBody Submission submission,
            Authentication auth) {

        var optionalExam = exams.findById(id);
        if (optionalExam.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var exam = optionalExam.get();
        int score = 0;

        for (int i = 0; i < exam.questions.size(); i++) {
            Integer answer = submission.answers() == null
                    ? null : submission.answers().get(String.valueOf(i));

            if (answer != null &&
                    answer.intValue() == exam.questions.get(i).correctIndex) {
                score++;
            }
        }

        String studentName = submission.studentName() == null ||
                submission.studentName().isBlank()
                ? auth.getName() : submission.studentName();

        var result = results.save(new ResultEntity(
                exam.title, studentName, auth.getName(),
                score, exam.questions.size()));

        return ResponseEntity.ok(resultDto(result));
    }

    @GetMapping("/results")
    public List<?> resultList(Authentication auth) {
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var rows = admin
                ? results.findAllByOrderBySubmittedAtDesc()
                : results.findAllByUsernameOrderBySubmittedAtDesc(auth.getName());

        return rows.stream().map(this::resultDto).toList();
    }

    @PostMapping("/admin/exams")
    public ResponseEntity<?> create(@RequestBody NewExam x) {
        if (x.title() == null || x.title().isBlank() ||
                x.questions() == null || x.questions().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Add a title and at least one question."));
        }

        ExamEntity exam = new ExamEntity(
                x.title(), x.description() == null ? "" : x.description(),
                x.durationMinutes() > 0 ? x.durationMinutes() : 10);

        for (var q : x.questions()) {
            if (q.text() == null || q.options() == null ||
                    q.options().size() != 4 ||
                    q.correctIndex() < 0 || q.correctIndex() > 3) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Each question needs text, four options and correct answer index 0-3."));
            }

            exam.questions.add(new QuestionEntity(
                    exam, q.text(), q.options().get(0),
                    q.options().get(1), q.options().get(2),
                    q.options().get(3), q.correctIndex()));
        }

        return ResponseEntity.status(201).body(dto(exams.save(exam)));
    }

    private Map<String, Object> dto(ExamEntity e) {
        return Map.of(
                "id", e.id,
                "title", e.title,
                "description", e.description,
                "durationMinutes", e.durationMinutes,
                "questions", e.questions.stream()
                        .map(q -> Map.of(
                                "text", q.text,
                                "options", List.of(
                                        q.optionA, q.optionB,
                                        q.optionC, q.optionD),
                                "correctIndex", q.correctIndex))
                        .toList());
    }

    private Map<String, Object> resultDto(ResultEntity r) {
        return Map.of(
                "id", r.id,
                "examTitle", r.examTitle,
                "studentName", r.studentName,
                "username", r.username,
                "score", r.score,
                "total", r.total,
                "submittedAt", r.submittedAt.toString());
    }

    public record Register(String username, String password) {}
    public record Submission(String studentName, Map<String, Integer> answers) {}
    public record NewQuestion(String text, List<String> options, int correctIndex) {}
    public record NewExam(String title, String description,
                          int durationMinutes, List<NewQuestion> questions) {}
}