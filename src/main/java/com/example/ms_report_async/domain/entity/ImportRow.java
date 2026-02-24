package com.example.ms_report_async.domain.entity;

import java.math.BigDecimal;
import java.util.Map;

public class ImportRow {
    private String ncm;
    private String paisOrigem;
    private BigDecimal volumeTotal;
    private BigDecimal valorTotal;
    private BigDecimal precoMedio;
    private BigDecimal freteMedio;

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public void setVolumeTotal(BigDecimal volumeTotal) {
        this.volumeTotal = volumeTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setPrecoMedio(BigDecimal precoMedio) {
        this.precoMedio = precoMedio;
    }

    public void setFreteMedio(BigDecimal freteMedio) {
        this.freteMedio = freteMedio;
    }

    public void setSeguroMedio(BigDecimal seguroMedio) {
        this.seguroMedio = seguroMedio;
    }

    private BigDecimal seguroMedio;

    public String getNcm() {
        return ncm;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public BigDecimal getVolumeTotal() {
        return volumeTotal;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }

    public BigDecimal getFreteMedio() {
        return freteMedio;
    }

    public BigDecimal getSeguroMedio() {
        return seguroMedio;
    }
}
