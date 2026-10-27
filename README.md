# ExamEase upgraded starter
Stack: Java 21, Spring Boot, Spring Data JPA, Spring Security Basic Auth, MySQL, React + Vite.

## Setup
1. In MySQL Workbench run: `CREATE DATABASE examease;`
2. Edit `backend/src/main/resources/application.properties` and set your local MySQL password. Never upload it to GitHub.
3. In one terminal: `cd backend` then `mvn spring-boot:run`
4. In another terminal: `cd frontend` then `npm install` then `npm run dev`
5. Open http://localhost:5173

Demo admin: `admin` / `Admin@123`. Change this before public deployment.
Student registration is available on the login page. Admin can create exams and view all results. Students can take exams and see their own results.

This is a local student-project starter, not production-hardened. Basic Auth requires HTTPS before deployment.
