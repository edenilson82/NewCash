package com.example.edeni.grana.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class Saldo  implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int ID;

    private double Saldo;

    private List<Operacao> operacao;

    public Saldo(int ID, double saldo, List<Operacao> operacao) {
        this.ID = ID;
        this.Saldo = saldo;
        this.operacao = operacao;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getSaldo() {
        return Saldo;
    }

    public void setSaldo(double saldo) {
        Saldo = saldo;
    }

    public List<Operacao> getCash() {
        return operacao;
    }

    public void setCash(List<Operacao> cash) {
        this.operacao = cash;
    }
}
