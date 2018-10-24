package com.example.edeni.grana.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.edeni.grana.model.Categoria;

import java.util.List;

@Dao
public interface CategoriaDao{

    @Insert
    public void inserir(Categoria categoria);

    @Delete
    public void delete(Categoria categoria);

    @Update
    public void alterar(Categoria categoria);

    @Query("SELECT * FROM Categoria")
    public List<Categoria> listar();

    @Query("SELECT * FROM Categoria WHERE id=:id")
    public Categoria procurarPorId(long id);
}
