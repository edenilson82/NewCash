package com.example.edeni.grana.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Endereco {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Integer ID;

    private String Rua;

    private String EnderecoCompleto;

    private String Latitude;

    private String Longitude;

    public Endereco(){}

    public Endereco(String enderecoCompleto, String rua, String latitude, String longitude) {
        EnderecoCompleto = enderecoCompleto;
        Rua = rua;
        Latitude = latitude;
        Longitude = longitude;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getEnderecoCompleto() {
        return EnderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        EnderecoCompleto = enderecoCompleto;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getRua() {
        return Rua;
    }

    public void setRua(String rua) {
        this.Rua = rua;
    }
}
