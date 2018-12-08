package com.example.edeni.grana.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.edeni.grana.model.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {

    @Insert
    public void inserir(Usuario usuario);

    @Update
    public void alterar(Usuario usuario);

    @Delete
    public void delete(Usuario usuario);

    @Query("SELECT * FROM Usuario")
    public List<Usuario> listar();

    @Query("SELECT * FROM Usuario WHERE id=:id")
    public Usuario procurarPorId(long id);

    @Query("SELECT * FROM Usuario WHERE username =:userName AND senha =:senha")
    public Usuario procurarPorUserName(String userName, String senha);

    @Query("SELECT * FROM Usuario WHERE email =:email AND senha =:senha")
    public Usuario procurarPorEmailSenha(String email, String senha);

    @Query("SELECT * FROM Usuario WHERE email =:email")
    public Usuario procurarPorEmail(String email);

    @Query("SELECT * FROM Usuario WHERE id_facebook =:id_facebook")
    public Usuario procurarPorIdFacebook(String id_facebook);

}
