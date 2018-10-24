package com.example.edeni.grana.model;

public class Bitcoin {

    private Double maiorPreco;
    private Double menorPreco;
    private Double quantidadeNegociada;
    private Double ultimaNegociacao;
    private Double maiorPrecoCompra;
    private Double menorPrecoVenda;
    private String data;

    public Bitcoin(){}

    public Bitcoin(Double maiorPreco, Double menorPreco, Double quantidadeNegociada, Double ultimaNegociacao, Double maiorPrecoCompra, Double menorPrecoVenda, String data) {
        this.maiorPreco = maiorPreco;
        this.menorPreco = menorPreco;
        this.quantidadeNegociada = quantidadeNegociada;
        this.ultimaNegociacao = ultimaNegociacao;
        this.maiorPrecoCompra = maiorPrecoCompra;
        this.menorPrecoVenda = menorPrecoVenda;
        this.data = data;
    }

    public Double getMaiorPreco() {
        return maiorPreco;
    }

    public void setMaiorPreco(Double maiorPreco) {
        this.maiorPreco = maiorPreco;
    }

    public Double getMenorPreco() {
        return menorPreco;
    }

    public void setMenorPreco(Double menorPreco) {
        this.menorPreco = menorPreco;
    }

    public Double getQuantidadeNegociada() {
        return quantidadeNegociada;
    }

    public void setQuantidadeNegociada(Double quantidadeNegociada) {
        this.quantidadeNegociada = quantidadeNegociada;
    }

    public Double getUltimaNegociacao() {
        return ultimaNegociacao;
    }

    public void setUltimaNegociacao(Double ultimaNegociacao) {
        this.ultimaNegociacao = ultimaNegociacao;
    }

    public Double getMaiorPrecoCompra() {
        return maiorPrecoCompra;
    }

    public void setMaiorPrecoCompra(Double maiorPrecoCompra) {
        this.maiorPrecoCompra = maiorPrecoCompra;
    }

    public Double getMenorPrecoVenda() {
        return menorPrecoVenda;
    }

    public void setMenorPrecoVenda(Double menorPrecoVenda) {
        this.menorPrecoVenda = menorPrecoVenda;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
