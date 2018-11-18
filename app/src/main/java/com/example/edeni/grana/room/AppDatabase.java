package com.example.edeni.grana.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;

import com.example.edeni.grana.dao.AlertaDao;
import com.example.edeni.grana.dao.CategoriaDao;
import com.example.edeni.grana.dao.EnderecoDao;
import com.example.edeni.grana.dao.LoginDao;
import com.example.edeni.grana.dao.OperacaoDao;
import com.example.edeni.grana.dao.PagamentoDao;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.model.Endereco;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.model.Pagamento;
import com.example.edeni.grana.model.Usuario;

import java.util.ArrayList;
import java.util.List;


@Database(entities = {Categoria.class,Operacao.class,Usuario.class,Pagamento.class, Endereco.class, Alerta.class}, version = 8)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public abstract CategoriaDao categoriaDao();
    public abstract OperacaoDao operacaoDao();
    public abstract LoginDao loginDao();
    //public abstract UsuarioDao usuarioDao();
    public abstract PagamentoDao pagamentoDao();
    public abstract EnderecoDao enderecoDao();
    public abstract AlertaDao alertaDao();

    public static AppDatabase getInstance(Context context) {
        if(appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cash")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }


    public static void destroyInstance() {
        appDatabase = null;
    }


}


