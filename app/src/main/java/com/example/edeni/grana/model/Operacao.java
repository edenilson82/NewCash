package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Dictionary;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "operacao",
        foreignKeys = {
                @ForeignKey(
                        entity = Categoria.class,
                        parentColumns = "id",
                        childColumns = "id_categoria",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Endereco.class,
                        parentColumns = "id",
                        childColumns = "id_endereco",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id",
                        childColumns = "id_usuario",
                        onDelete = CASCADE
                )
        }
)
public class Operacao implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int ID;

    private String Descricao;
    private String Tipo;
    private String Data;
    private Double Valor;

    @ColumnInfo(name = "id_categoria")
    private long Categoria_Id;

    @ColumnInfo(name = "id_endereco")
    private Integer Endereco_Id;

    @ColumnInfo(name = "id_usuario")
    private Integer Usuario_id;

    public Operacao(){}

    public Operacao(String descricao, String tipo, String data, Double valor, long categoria_Id) {
        Descricao = descricao;
        Tipo = tipo;
        Data = data;
        Valor = valor;
        Categoria_Id = categoria_Id;
    }

    public Operacao(String descricao, String tipo, String data, Double valor, long categoria_Id, Integer endereco_Id) {
        Descricao = descricao;
        Tipo = tipo;
        Data = data;
        Valor = valor;
        Categoria_Id = categoria_Id;
        Endereco_Id = endereco_Id;
    }

    public Operacao(String descricao, String tipo, String data, Double valor, long categoria_Id, Integer endereco_Id, Integer usuario_id) {
        Descricao = descricao;
        Tipo = tipo;
        Data = data;
        Valor = valor;
        Categoria_Id = categoria_Id;
        Endereco_Id = endereco_Id;
        Usuario_id = usuario_id;
    }

    public int getID() {
        return ID;
    }

    public void setID(@NonNull int ID) {
        this.ID = ID;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(@NonNull String tipo) {
        Tipo = tipo;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public Double getValor() {
        return Valor;
    }

    public void setValor(@NonNull Double valor) {
        Valor = valor;
    }

    public long getCategoria_Id() {
        return Categoria_Id;
    }

    public void setCategoria_Id(long categoria_Id) {
        Categoria_Id = categoria_Id;
    }

    public Integer getEndereco_Id() {
        return Endereco_Id;
    }

    public void setEndereco_Id(Integer endereco_Id) {
        Endereco_Id = endereco_Id;
    }

    public Integer getUsuario_id() {
        return Usuario_id;
    }

    public void setUsuario_id(Integer usuario_id) {
        Usuario_id = usuario_id;
    }
}
