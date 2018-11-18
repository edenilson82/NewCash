package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Alerta implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Integer ID;

    private String Tipo;
    private int Condicao;  // 1 MENOR E 2  MAIOR
    private Double Valor;

    public Alerta() {}

    public Alerta(String tipo, int condicao, Double valor) {
        Tipo = tipo;
        Condicao = condicao;
        Valor = valor;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public int getCondicao() {
        return Condicao;
    }

    public void setCondicao(int condicao) {
        Condicao = condicao;
    }

    public Double getValor() {
        return Valor;
    }

    public void setValor(Double valor) {
        Valor = valor;
    }
}
