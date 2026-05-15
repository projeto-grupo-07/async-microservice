package com.example.ms_report_async.infraestructure.rest.impl;

import com.example.ms_report_async.infraestructure.rest.controller.HealthCheckController;
import org.springframework.http.ResponseEntity;

public class HealthCheckControllerImpl implements HealthCheckController {
    @Override
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Ok!");
    }
}
