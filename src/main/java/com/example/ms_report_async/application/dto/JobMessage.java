package com.example.ms_report_async.application.dto;

import java.io.Serializable;

public record JobMessage(Integer ano, Integer mes, String jobId, String fileKey) implements Serializable {}
