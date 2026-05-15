package com.example.ms_report_async.infraestructure.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/health")
public interface HealthCheckController {
    ResponseEntity<String> health();
}
