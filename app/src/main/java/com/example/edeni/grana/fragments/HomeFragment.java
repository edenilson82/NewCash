package com.example.edeni.grana.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.edeni.grana.App;
import com.example.edeni.grana.CadastroActivity;
import com.example.edeni.grana.MainActivity;
import com.example.edeni.grana.R;
import com.example.edeni.grana.adapter.OperacaoAdapter;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.model.Usuario;
import com.example.edeni.grana.receiver.ReceiverInternet;
import com.example.edeni.grana.room.AppDatabase;
import com.example.edeni.grana.services.ServiceAccessInternet;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment{

    private static String TITULO = "Movimentos";

    private AppDatabase db;

    FloatingActionButton floatingActionButton;

    RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    List<Operacao> operacoes;

    Usuario usuario;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(TITULO);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View _view = inflater.inflate(R.layout.fragment_home, container, false);

        // chama o service que verifica se há Conexão com a Internet
        getContext().startService(new Intent(getContext(), ServiceAccessInternet.class));

        db = AppDatabase.getInstance(getActivity());

        floatingActionButton = (FloatingActionButton) _view.findViewById(R.id.btnFab);

        Activity main = (MainActivity)this.getActivity();
        usuario = ((MainActivity) main).getUsuarioLogado();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCadastroOperacao(usuario);
            }
        });

        recyclerView = (RecyclerView) _view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // preenche a lista na tela
        preecheOperacoes();

        return _view;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        preecheOperacoes();
    }

    @Override
    public void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    public void abrirCadastroOperacao(Usuario usuario){

        // CHAMADA IMPLICITAMENTE
        Intent intent = new Intent("android.intent.action.CadastroActivity");
        intent.putExtra("usuario",usuario);
        startActivity(intent);
    }

    public void preecheOperacoes() {
        new AsyncTaskImpl().execute();
    }

    public class AsyncTaskImpl extends AsyncTask<Void, Void, List<Operacao>> {

        @Override
        protected List<Operacao> doInBackground(Void... voids) {

            try{
                Integer id_usuario = usuario.getID().intValue();
                operacoes = db.operacaoDao().listar(id_usuario);
                return operacoes;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Operacao> operacoes) {
            //volta da main thread
            try {
                mAdapter = new OperacaoAdapter(getContext(), operacoes);
                recyclerView.setAdapter(mAdapter);
            }catch (Exception ex){
                Toast.makeText(App.getContext(), "Não foi possível carregar a lista, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
