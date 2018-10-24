package com.example.edeni.grana.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Pagamento implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int ID;

    private int Tipo;
    private int Quantidate_Parcelas;
    private Double Valor;

    public Pagamento(){}

    public Pagamento(int ID, int tipo, int quantidate_Parcelas, Double valor) {
        this.ID = ID;
        Tipo = tipo;
        Quantidate_Parcelas = quantidate_Parcelas;
        Valor = valor;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTipo() {
        return Tipo;
    }

    public void setTipo(int tipo) {
        Tipo = tipo;
    }

    public int getQuantidate_Parcelas() {
        return Quantidate_Parcelas;
    }

    public void setQuantidate_Parcelas(int quantidate_Parcelas) {
        Quantidate_Parcelas = quantidate_Parcelas;
    }

    public Double getValor() {
        return Valor;
    }

    public void setValor(Double valor) {
        Valor = valor;
    }
}
