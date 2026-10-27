package com.examease;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Table(name="app_users") class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(unique=true,nullable=false) public String username;
 @Column(nullable=false) public String password;
 @Column(nullable=false) public String role;
 protected AppUser(){} AppUser(String u,String p,String r){username=u;password=p;role=r;}
}
@Entity @Table(name="exams") class ExamEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String title;
 @Column(nullable=false,length=1000) public String description;
 public int durationMinutes=10;
 @OneToMany(mappedBy="exam",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER) @OrderBy("id ASC") public List<QuestionEntity> questions=new ArrayList<>();
 protected ExamEntity(){} ExamEntity(String t,String d,int m){title=t;description=d;durationMinutes=m;}
}
@Entity @Table(name="exam_questions") class QuestionEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="exam_id",nullable=false) public ExamEntity exam;
 @Column(nullable=false,length=1000) public String text;
 @Column(nullable=false) public String optionA; @Column(nullable=false) public String optionB; @Column(nullable=false) public String optionC; @Column(nullable=false) public String optionD;
 public int correctIndex;
 protected QuestionEntity(){} QuestionEntity(ExamEntity e,String t,String a,String b,String c,String d,int i){exam=e;text=t;optionA=a;optionB=b;optionC=c;optionD=d;correctIndex=i;}
}
@Entity @Table(name="exam_results") class ResultEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String examTitle; public String studentName; public String username; public int score; public int total; public LocalDateTime submittedAt;
 protected ResultEntity(){} ResultEntity(String e,String n,String u,int s,int t){examTitle=e;studentName=n;username=u;score=s;total=t;submittedAt=LocalDateTime.now();}
}
