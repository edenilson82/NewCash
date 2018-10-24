package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Usuario implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(name = "nome")
    private String Nome;

    @ColumnInfo(name = "sobrenome")
    private String SobreNome;

    @ColumnInfo(name = "email")
    private String Email;

    @ColumnInfo(name = "username")
    private String UserName;

    @ColumnInfo(name = "nascimento")
    private String Nascimento;

    @ColumnInfo(name = "idade")
    private Integer Idade;

    @ColumnInfo(name = "senha")
    private String Senha;

    @ColumnInfo(name = "conectado")
    private boolean Conectado;

    public Usuario() { }

    public Usuario(String userName, String senha, boolean conectado) {
        UserName = userName;
        Senha = senha;
        Conectado = conectado;
    }

    public Usuario(String username, String senha) {
        UserName = username;
        Senha = senha;
    }

    public Usuario(String nome, String sobreNome, String userName, String nascimento, Integer idade, String senha,boolean conectado) {
       // this.ID = ID;
        Nome = nome;
        SobreNome = sobreNome;
        UserName = userName;
        Nascimento = nascimento;
        Idade = idade;
        Senha = senha;
        Conectado = conectado;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getSobreNome() {
        return SobreNome;
    }

    public void setSobreNome(String sobreNome) {
        SobreNome = sobreNome;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getNascimento() {
        return Nascimento;
    }

    public void setNascimento(String nascimento) {
        Nascimento = nascimento;
    }

    public Integer getIdade() {
        return Idade;
    }

    public void setIdade(Integer idade) {
        Idade = idade;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isConectado() {
        return Conectado;
    }

    public void setConectado(boolean conectado) {
        Conectado = conectado;
    }
}
