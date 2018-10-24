package com.example.edeni.grana.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

;

import com.example.edeni.grana.model.Operacao;

import java.util.List;

@Dao
public interface OperacaoDao {

    @Insert
    public void inserir(Operacao... operacao);

    @Delete
    public void delete(Operacao operacao);

    @Update
    public void alterar(Operacao operacao);

    @Query("SELECT * FROM operacao")
    public List<Operacao> listar();

    @Query("SELECT * FROM operacao WHERE id=:id")
    public Operacao procurarPorId(int id);
}
