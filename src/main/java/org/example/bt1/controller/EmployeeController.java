package org.example.bt1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @GetMapping("/profile")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("API nghiệp vụ nhân sự truy cập thành công");
    }
}
