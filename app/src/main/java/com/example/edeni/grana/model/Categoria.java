package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Categoria {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(name = "nome")
    private String NomeCategoria;

    public Categoria() {   }

    public Categoria(long ID) {
        this.ID = ID;
    }

    public Categoria(String nome) {
        NomeCategoria = nome;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getNomeCategoria() {
        return NomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        NomeCategoria = nomeCategoria;
    }
}
