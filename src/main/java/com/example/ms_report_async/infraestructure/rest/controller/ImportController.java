package com.example.ms_report_async.infraestructure.rest.controller;

import com.example.ms_report_async.application.dto.JobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/import")
@Tag(name = "Import", description = "Endpoints para importação de arquivos")
public interface ImportController {

    @PostMapping("/{*fileKey}")
    @Operation(summary = "Iniciar importação de arquivo", description = "Enfileira uma tarefa de importação assincrona para o arquivo especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Requisição aceita - importação enfileirada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<JobResponse> importFile(@PathVariable String fileKey);

    @GetMapping("/report/{*jobId}")
    @Operation(summary = "Baixar relatório PDF", description = "Retorna o PDF gerado para um Job ID específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<byte[]> getReport(@PathVariable String jobId);
}
