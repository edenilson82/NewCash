package com.example.edeni.grana.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.edeni.grana.model.Pagamento;
import java.util.List;

@Dao
public interface PagamentoDao {

    @Insert
    public void inserir(Pagamento pagamento);

    @Delete
    public void delete(Pagamento pagamento);

    @Update
    public void alterar(Pagamento pagamento);

    @Query("SELECT * FROM Pagamento")
    public List<Pagamento> listar();

    @Query("SELECT * FROM Pagamento WHERE id=:id")
    public Pagamento procurarPorId(long id);
}
