package com.example.edeni.grana.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class Extrato implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int ID;

    private List<Operacao> operacao;

    public Extrato(int ID, List<Operacao> cash) {
        this.ID = ID;
        this.operacao = cash;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<Operacao> getOperacao() {
        return operacao;
    }

    public void setCash(List<Operacao> operacao) {
        this.operacao = operacao;
    }

}
