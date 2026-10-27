package com.examease;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
interface UserRepository extends JpaRepository<AppUser,Long>{Optional<AppUser> findByUsername(String username);boolean existsByUsername(String username);}
interface ExamRepository extends JpaRepository<ExamEntity,Long>{}
interface ResultRepository extends JpaRepository<ResultEntity,Long>{List<ResultEntity> findAllByUsernameOrderBySubmittedAtDesc(String username);List<ResultEntity> findAllByOrderBySubmittedAtDesc();}
