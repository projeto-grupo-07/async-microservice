package com.example.ms_report_async.infraestructure.rest;

import com.example.ms_report_async.application.dto.JobResponse;
import com.example.ms_report_async.infraestructure.async.RabbitImportProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
public class ImportController {

    private final RabbitImportProducer producer;

    public ImportController(RabbitImportProducer producer) {
        this.producer = producer;
    }


    @PostMapping("/{fileKey}")
    public ResponseEntity<JobResponse> importFile(@PathVariable String fileKey) {
        String jobId = producer.publish(fileKey);
        return ResponseEntity.accepted().body(new JobResponse(jobId));
    }


}
