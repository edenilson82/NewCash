package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "alerta",
        foreignKeys = {
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id",
                        childColumns = "id_usuario",
                        onDelete = CASCADE
                )
        }
)
public class Alerta implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Integer ID;

    private String Tipo;
    private int Condicao;  // 1 MENOR E 2  MAIOR
    private Double Valor;

    @ColumnInfo(name = "id_usuario")
    private Integer Id_usuario;

    public Alerta() {}

    public Alerta(String tipo, int condicao, Double valor, Integer id_usuario) {
        Tipo = tipo;
        Condicao = condicao;
        Valor = valor;
        Id_usuario = id_usuario;
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

    public Integer getId_usuario() {
        return Id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.Id_usuario = id_usuario;
    }
}
