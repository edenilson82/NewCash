package com.example.edeni.grana.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.edeni.grana.model.Usuario;

import java.util.List;

@Dao
public interface LoginDao {

    @Insert
    public void inserir(Usuario usuario);

    @Update
    public void alterar(Usuario usuario);

    @Query("DELETE FROM usuario")
    public void deleteAll();

    @Delete
    public void delete(Usuario usuario);

    @Query("SELECT * FROM Usuario")
    public List<Usuario> listar();

    @Query("SELECT * FROM Usuario WHERE id=:id")
    public Usuario procurarPorId(long id);

    @Query("SELECT * FROM Usuario WHERE username =:userName AND senha =:senha")
    public Usuario procurarPorUserName(String userName, String senha);
}
