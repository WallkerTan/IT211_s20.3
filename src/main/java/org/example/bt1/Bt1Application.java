package org.example.bt1;

import org.example.bt1.entity.Employee;
import org.example.bt1.entity.Role;
import org.example.bt1.repository.EmployeeRepository;
import org.example.bt1.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class Bt1Application {

    public static void main(String[] args) {
        SpringApplication.run(Bt1Application.class, args);
    }

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               EmployeeRepository employeeRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (employeeRepository.findByUsername("admin").isEmpty()) {
                Role roleAdmin = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());

                Employee admin = Employee.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .active(true)
                        .roles(Set.of(roleAdmin))
                        .build();

                employeeRepository.save(admin);
            }
        };
    }
}
