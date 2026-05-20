package com.example.ms_report_async.infraestructure.rest.controller;

import com.example.ms_report_async.application.dto.ReportRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/import")
@Tag(name = "Import", description = "Endpoints para importação de arquivos")
public interface ImportController {
    @GetMapping("/report")
    @Operation(summary = "Baixar relatório PDF", description = "Retorna o PDF gerado para um Job ID específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<byte[]> getReport(@ModelAttribute ReportRequestDto reportRequestDto);
}
