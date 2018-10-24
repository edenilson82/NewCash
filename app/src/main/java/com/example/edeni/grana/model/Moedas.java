package com.example.edeni.grana.model;

public class Moedas {
/*
    bid	= Compra
    ask	= 	Venda
    varBid	= 	Variação
    pctChange	= 	Porcentagem de Variação
    high	= 	Máximo
    low		=  Mínimo
    */

    private String Moeda;
    private Double ValorMaximo;
    private Double ValorMinimo;
    private Double ValorCompra;
    private Double ValorVenda;
    private Double Variacao;
    private String Data;

    public String getMoeda() {
        return Moeda;
    }

    public void setMoeda(String moeda) {
        Moeda = moeda;
    }


    public Double getValorMaximo() {
        return ValorMaximo;
    }

    public void setValorMaximo(Double valorMaximo) {
        ValorMaximo = valorMaximo;
    }

    public Double getValorMinimo() {
        return ValorMinimo;
    }

    public void setValorMinimo(Double valorMinimo) {
        ValorMinimo = valorMinimo;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public Double getValorCompra() {
        return ValorCompra;
    }

    public void setValorCompra(Double valorCompra) {
        ValorCompra = valorCompra;
    }

    public Double getValorVenda() {
        return ValorVenda;
    }

    public void setValorVenda(Double valorVenda) {
        ValorVenda = valorVenda;
    }

    public Double getVariacao() {
        return Variacao;
    }

    public void setVariacao(Double variacao) {
        Variacao = variacao;
    }
}
