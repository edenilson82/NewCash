package com.example.edeni.grana.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edeni.grana.App;
import com.example.edeni.grana.R;
import com.example.edeni.grana.adapter.AlertasAdapter;
import com.example.edeni.grana.adapter.OperacaoAdapter;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.room.AppDatabase;
import com.example.edeni.grana.services.ServiceAccessInternet;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertasFragment extends Fragment {

    private static String TITULO = "Alertas";

    private AppDatabase db;

    FloatingActionButton btnFabCadastroAlertas;

    public RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    Alerta alerta;
    List<Alerta> listaDeAlertas;

    boolean isRadioCondicaoAlertaChecked;
    boolean isRadioTipoDeAlertChecked;
    EditText txtValorAlerta;
    RadioButton radioButtonTipoSaldo;
    RadioButton radioButtonTipoCredito;
    RadioButton radioButtonTipoDespesa;
    RadioButton radioButtonCondicaoMaiorQue;
    RadioButton radioButtonCondicaoMenorQue;

    public AlertasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(TITULO);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View _view = inflater.inflate(R.layout.fragment_alertas, container, false);

        db = AppDatabase.getInstance(getActivity());

        btnFabCadastroAlertas = (FloatingActionButton) _view.findViewById(R.id.btnFabCadastroAlertas);

        txtValorAlerta = (EditText) _view.findViewById(R.id.txt_valor_alerta);
        radioButtonTipoCredito = (RadioButton) _view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoDespesa = (RadioButton) _view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoSaldo = (RadioButton) _view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonCondicaoMaiorQue = (RadioButton) _view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonCondicaoMenorQue = (RadioButton) _view.findViewById(R.id.radio_tipo_operacao_credito);

        alerta = (Alerta) getActivity().getIntent().getSerializableExtra("alerta");

        if(alerta != null){
                txtValorAlerta.setText(Double.toString(alerta.getValor()));

                if(alerta.getTipo().equals("saldo")){
                    radioButtonTipoSaldo.setChecked(true);
                }else if(alerta.getTipo().equals("credito")){
                    radioButtonTipoCredito.setChecked(true);
                }else{
                    radioButtonTipoDespesa.setChecked(true);
                }

                if(alerta.getCondicao() <= 0){
                    radioButtonCondicaoMenorQue.setChecked(true);
                }else{
                    radioButtonCondicaoMaiorQue.setChecked(true);
                }
        }


        btnFabCadastroAlertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastroAlertasDialog();
            }
        });



        recyclerView = (RecyclerView) _view.findViewById(R.id.recyclerViewAlerta);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        preecheAlertas();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        preecheAlertas();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private String getTipoAlertaSelecionado(boolean isSaldo, boolean isCredito, boolean isDespesa){
        String tipoSelecionado = null;
        if(isSaldo)
            tipoSelecionado = "saldo";

        if(isCredito)
            tipoSelecionado = "credito";

        if(isDespesa)
            tipoSelecionado = "despesa";

        return tipoSelecionado;
    }

    private void cadastroAlertasDialog() {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.layout_cadastro_alertas);

        //instancia os objetos que estão no layout customdialog.xml
        final Button confirmar = (Button) dialog.findViewById(R.id.btn_confirmar_alerta);
       // final Button cancelar = (Button) dialog.findViewById(R.id.btn_cancelar_alerta);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // saldo, credito, despesa
                radioButtonTipoSaldo = (RadioButton) dialog.findViewById(R.id.radio_saldo);
                radioButtonTipoCredito = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_credito);
                radioButtonTipoDespesa = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_despesa);
                radioButtonCondicaoMaiorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_maior);
                radioButtonCondicaoMenorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_menor);
                txtValorAlerta = (EditText) dialog.findViewById(R.id.txt_valor_alerta);

                boolean isCheckedMaior = radioButtonCondicaoMaiorQue.isChecked();
                boolean isCheckedMenor = radioButtonCondicaoMenorQue.isChecked();
                boolean isCheckedSaldo = radioButtonTipoSaldo.isChecked();
                boolean isCheckedCredito = radioButtonTipoCredito.isChecked();
                boolean isCheckedDespesa = radioButtonTipoDespesa.isChecked();

                String tipo = getTipoAlertaSelecionado(isCheckedSaldo, isCheckedCredito, isCheckedDespesa);
                int condicao = isCheckedMaior ? 2 : isCheckedMenor ? 1 : 0; // 2 MAIOR e 1 MENOR
                String valor = txtValorAlerta.getText().toString();
                boolean salvo = false;
                try {
                    salvo = salvaAlerta(tipo, condicao, valor);

                    if(salvo)
                       dialog.dismiss();

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });

        /*
        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finaliza o dialog
                dialog.dismiss();
            }
        });
        */

        //exibe na tela o dialog
        dialog.show();
    }

    private boolean salvaAlerta(String tipoAlerta, int condicao, String valor){

        boolean salvo = false;
        double valorAlerta = 0;

        if(tipoAlerta == null || tipoAlerta.equals("")){
            Toast.makeText(App.getContext(), "Selecione um tipo de Alerta.", Toast.LENGTH_SHORT).show();
            return salvo;
        }

        if(condicao <= 0){
            Toast.makeText(App.getContext(), "Selecione uma condição.", Toast.LENGTH_SHORT).show();
            return salvo;
        }

        if(valor == null || Double.parseDouble(valor) < 0) {
            Toast.makeText(App.getContext(), "Digíte um valor válido.", Toast.LENGTH_SHORT).show();
            return salvo;
        }else{
            valorAlerta = Double.parseDouble(valor);
        }

        Alerta alerta = new Alerta(tipoAlerta, condicao, valorAlerta);
        db.alertaDao().inserir(alerta);
        salvo = true;
        Toast.makeText(App.getContext(), "Alerta criado com sucesso.", Toast.LENGTH_SHORT).show();
        return salvo;
    }


    public void preecheAlertas() {
        new AsyncTaskImpl().execute();
    }

    public class AsyncTaskImpl extends AsyncTask<Void, Void, List<Alerta>> {

        @Override
        protected List<Alerta> doInBackground(Void... voids) {

            listaDeAlertas = db.alertaDao().listar();
            return listaDeAlertas;
        }

        @Override
        protected void onPostExecute(List<Alerta> alertas) {
            //volta da main thread
            try {
                mAdapter = new AlertasAdapter(getContext(), alertas);
                recyclerView.setAdapter(mAdapter);

            }catch (Exception ex){
                Toast.makeText(App.getContext(), "Não foi possível carregar a lista, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
