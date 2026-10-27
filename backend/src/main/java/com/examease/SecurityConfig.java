package com.examease;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.*;
@Configuration public class SecurityConfig {
 @Bean UserDetailsService users(UserRepository repo){return name->repo.findByUsername(name).map(u->User.withUsername(u.username).password(u.password).roles(u.role).build()).orElseThrow(()->new UsernameNotFoundException("User not found"));}
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception{return http.csrf(c->c.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(a->a.requestMatchers(HttpMethod.POST,"/api/auth/register").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers(HttpMethod.POST,"/api/exams/*/submit").hasRole("STUDENT").requestMatchers("/api/**").authenticated().anyRequest().permitAll()).httpBasic(Customizer.withDefaults()).build();}
 @Bean WebMvcConfigurer cors(){return new WebMvcConfigurer(){public void addCorsMappings(CorsRegistry r){r.addMapping("/api/**").allowedOrigins("http://localhost:5173").allowedMethods("GET","POST","OPTIONS").allowedHeaders("*");}};}
}
