package com.example.edeni.grana.fragments;


import android.app.Activity;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edeni.grana.App;
import com.example.edeni.grana.MainActivity;
import com.example.edeni.grana.R;
import com.example.edeni.grana.adapter.AlertasAdapter;
import com.example.edeni.grana.adapter.OperacaoAdapter;
import com.example.edeni.grana.dao.ItemClickListener;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.model.Usuario;
import com.example.edeni.grana.room.AppDatabase;
import com.example.edeni.grana.services.ServiceAccessInternet;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import static android.support.v7.widget.helper.ItemTouchHelper.*;

import java.text.NumberFormat;
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

    EditText txtValorAlerta;
    RadioButton radioButtonTipoSaldo;
    RadioButton radioButtonTipoCredito;
    RadioButton radioButtonTipoDespesa;
    RadioButton radioButtonCondicaoMaiorQue;
    RadioButton radioButtonCondicaoMenorQue;
    Activity main;
    Usuario usuario;

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

        main = (MainActivity)this.getActivity();
        usuario = ((MainActivity) main).getUsuarioLogado();

        /*
        if(alerta != null){

            if(txtValorAlerta != null)
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
*/

        btnFabCadastroAlertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastroAlertasDialog(usuario);
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

    private void cadastroAlertasDialog(final Usuario usuario) {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.layout_cadastro_alertas);

        //instancia os objetos que estão no layout customdialog.xml
        final Button confirmar = (Button) dialog.findViewById(R.id.btn_confirmar_alerta);
        // final Button cancelar = (Button) dialog.findViewById(R.id.btn_cancelar_alerta);1
        final Button excluir = (Button) dialog.findViewById(R.id.btn_excluir_alerta);

        excluir.setVisibility(View.GONE);

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
                    int id_usuario = usuario.getID();
                    salvo = salvaAlerta(tipo, condicao, valor, id_usuario);

                    if(salvo){
                        dialog.dismiss();
                        preecheAlertas();
                    }

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

    public void alterarAlertaDialog(final Alerta alertaSelecionado) {

        //  NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        final Dialog dialog= new Dialog(getContext());

        dialog.setContentView(R.layout.layout_cadastro_alertas);

        radioButtonTipoSaldo = (RadioButton) dialog.findViewById(R.id.radio_saldo);
        radioButtonTipoCredito = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoDespesa = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_despesa);
        radioButtonCondicaoMaiorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_maior);
        radioButtonCondicaoMenorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_menor);
        txtValorAlerta = (EditText) dialog.findViewById(R.id.txt_valor_alerta);

        int condicao = alertaSelecionado.getCondicao();
        String tipo = alertaSelecionado.getTipo();
        String valor = String.valueOf(alertaSelecionado.getValor());
        carregaDadosNaTela(condicao, tipo, valor);

        final Button confirmar = (Button) dialog.findViewById(R.id.btn_confirmar_alerta);
        final Button excluir = (Button) dialog.findViewById(R.id.btn_excluir_alerta);

        if(alertaSelecionado.getID() > 0 ){
            confirmar.setText("Alterar");
        }
        //final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar);

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

                int id_alerta = 0;
                if(alertaSelecionado != null){
                    id_alerta = alertaSelecionado.getID();
                }

                String tipo = getTipoAlertaSelecionado(isCheckedSaldo, isCheckedCredito, isCheckedDespesa);
                int condicao = isCheckedMaior ? 2 : isCheckedMenor ? 1 : 0; // 2 MAIOR e 1 MENOR
                String valor = txtValorAlerta.getText().toString();
                boolean salvo = false;
                try {
                    salvo = alteraAlerta(id_alerta, tipo, condicao, valor);

                    if(usuario == null)
                        usuario = db.usuarioDao().procurarPorId(alertaSelecionado.getId_usuario());

                    if(salvo){
                        dialog.dismiss();
                        preecheAlertas();
                    }


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
        });*/
        //exibe na tela o dialog


        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(alertaSelecionado != null){
                    db.alertaDao().delete(alertaSelecionado);
                    dialog.dismiss();
                    Toast.makeText(App.getContext(), "Alerta removido com sucesso.", Toast.LENGTH_SHORT).show();
                }
                preecheAlertas();

            }
        });

        dialog.show();
    }

    private boolean alteraAlerta(int id_alerta, String tipoAlerta, int condicao, String valor){

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

        try{

            Alerta alerta = db.alertaDao().procurarPorId(id_alerta);

            if(alerta != null){
                alerta.setCondicao(condicao);
                alerta.setTipo(tipoAlerta);
                alerta.setValor(valorAlerta);

                db.alertaDao().alterar(alerta);

                salvo = true;
            }else{
                Toast.makeText(App.getContext(), "Erro ao alterar o registro.", Toast.LENGTH_SHORT).show();
                return salvo;
            }

        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(App.getContext(), "Erro ao alterar o registro." + ex.getMessage(), Toast.LENGTH_SHORT).show();
            return salvo;
        }

        Toast.makeText(App.getContext(), "Alerta alterado com sucesso.", Toast.LENGTH_SHORT).show();
        return salvo;
    }

    private boolean salvaAlerta(String tipoAlerta, int condicao, String valor, Integer id_usuario){

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

        Alerta alerta = new Alerta(tipoAlerta, condicao, valorAlerta, id_usuario);
        db.alertaDao().inserir(alerta);
        salvo = true;
        Toast.makeText(App.getContext(), "Alerta criado com sucesso.", Toast.LENGTH_SHORT).show();
        return salvo;
    }

    private void carregaDadosNaTela(int condicao, String tipo, String valor){

        if("saldo".equals(tipo)){
            radioButtonTipoSaldo.setChecked(true);
        }else if("credito".equals(tipo)){
            radioButtonTipoCredito.setChecked(true);
        }else{
            radioButtonTipoDespesa.setChecked(true);
        }

        if(condicao == 1){
            radioButtonCondicaoMenorQue.setChecked(true);
        }else{
            radioButtonCondicaoMaiorQue.setChecked(true);
        }

        if(!valor.equals("")){
            txtValorAlerta.setText(valor);
        }

    }

    public void preecheAlertas() {
        new AsyncTaskImpl().execute();
    }


    public class AsyncTaskImpl extends AsyncTask<Void, Void, List<Alerta>> {

        @Override
        protected List<Alerta> doInBackground(Void... voids) {

            try{
                if(usuario != null){
                    int id_usuario = usuario.getID();
                    listaDeAlertas = db.alertaDao().listar(id_usuario);
                    return listaDeAlertas;
                }
            }catch(Exception ex){
                Toast.makeText(App.getContext(), "Não foi possível carregar a lista, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Alerta> alertas) {
            //volta da main thread
            try {
                mAdapter = new AlertasAdapter(main, alertas, new AlertasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Alerta item) {
                        alterarAlertaDialog(item);
                        preecheAlertas();
                    }
                });
                recyclerView.setAdapter(mAdapter);

            }catch (Exception ex){
                Toast.makeText(App.getContext(), "Não foi possível carregar a lista, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
