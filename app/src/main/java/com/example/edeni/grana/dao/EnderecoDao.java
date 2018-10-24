package com.example.edeni.grana.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.edeni.grana.model.Endereco;

import java.util.List;

@Dao
public interface EnderecoDao {

    @Insert
    public void inserir(Endereco endereco);

    @Update
    public void alterar(Endereco endereco);

    @Query("DELETE FROM endereco")
    public void deleteAll();

    @Delete
    public void delete(Endereco endereco);

    @Query("SELECT * FROM Endereco")
    public List<Endereco> listar();

    @Query("SELECT * FROM Endereco WHERE id=:id")
    public Endereco procurarPorId(long id);

    @Query("SELECT * FROM Endereco WHERE latitude =:latitude AND longitude =:longitude")
    public Endereco procurarPorLatLong(String latitude, String longitude);
}
