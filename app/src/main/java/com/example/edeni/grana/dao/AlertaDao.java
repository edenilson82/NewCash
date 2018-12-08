package com.example.edeni.grana.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Categoria;

import java.util.List;

@Dao
public interface AlertaDao {

    @Insert
    public void inserir(Alerta alerta);

    @Delete
    public void delete(Alerta alerta);

    @Query("DELETE FROM alerta")
    public void deleteAll();

    @Update
    public void alterar(Alerta alerta);

    @Query("SELECT * FROM Alerta WHERE id_usuario=:id_usuario")
    public List<Alerta> listar(Integer id_usuario);

    @Query("SELECT * FROM Alerta WHERE id=:id")
    public Alerta procurarPorId(long id);
}
