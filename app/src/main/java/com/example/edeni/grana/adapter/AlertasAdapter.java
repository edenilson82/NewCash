package com.example.edeni.grana.adapter;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
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
import com.example.edeni.grana.dao.OnAlertasListener;
import com.example.edeni.grana.fragments.AlertasFragment;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.room.AppDatabase;

import java.text.NumberFormat;
import java.util.List;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.ViewHolder> implements OnAlertasListener {

    AppDatabase db;
    private List<Alerta> listaDeAlertas;
    private Context context;

    EditText txtValorAlerta;
    RadioButton radioButtonTipoSaldo;
    RadioButton radioButtonTipoCredito;
    RadioButton radioButtonTipoDespesa;
    RadioButton radioButtonCondicaoMaiorQue;
    RadioButton radioButtonCondicaoMenorQue;

    AlertasAdapter.ViewHolder viewHolder;
    View newView;

    public AlertasAdapter(Context context, List<Alerta> _listaAlertas) {
        this.listaDeAlertas = _listaAlertas;
        this.context = context;
    }

    public void setListaAlertas(List<Alerta> listaAlertas) {
        this.listaDeAlertas = listaAlertas;
    }


    @NonNull
    @Override
    public AlertasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_alertas, parent, false);
        viewHolder = new AlertasAdapter.ViewHolder(view, this);
        newView = view;

        txtValorAlerta = (EditText) view.findViewById(R.id.txt_valor_alerta);
        radioButtonTipoCredito = (RadioButton) view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoDespesa = (RadioButton) view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoSaldo = (RadioButton) view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonCondicaoMaiorQue = (RadioButton) view.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonCondicaoMenorQue = (RadioButton) view.findViewById(R.id.radio_tipo_operacao_credito);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AlertasAdapter.ViewHolder holder, int position) {
        db = AppDatabase.getInstance(context);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        holder.valor.setText(numberFormat.format(listaDeAlertas.get(position).getValor()));

        String tipo = (listaDeAlertas.get(position).getTipo().substring(0, 1).toUpperCase() + (listaDeAlertas.get(position).getTipo().substring(1)));
        holder.tipo.setText(tipo);

        String sinalCondicao = listaDeAlertas.get(position).getCondicao() == 1 ? " < " : " > ";
        holder.condicao.setText(sinalCondicao);
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

    @Override
    public int getItemCount() {
        return listaDeAlertas.size();
    }

    @Override
    public void onFondoClicked(int position) {

        Alerta alertaSelecionado = listaDeAlertas.get(position);
        cadastroDeAlertaDialog(alertaSelecionado);

    }

    public void cadastroDeAlertaDialog(final Alerta alertaSelecionado) {

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        final Dialog dialog = new Dialog(this.context);

        dialog.setContentView(R.layout.layout_cadastro_alertas);

        radioButtonTipoSaldo = (RadioButton) dialog.findViewById(R.id.radio_saldo);
        radioButtonTipoCredito = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_credito);
        radioButtonTipoDespesa = (RadioButton) dialog.findViewById(R.id.radio_tipo_operacao_despesa);
        radioButtonCondicaoMaiorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_maior);
        radioButtonCondicaoMenorQue = (RadioButton) dialog.findViewById(R.id.radio_condicao_menor);
        txtValorAlerta = (EditText) dialog.findViewById(R.id.txt_valor_alerta);

        int condicao = alertaSelecionado.getCondicao();
        String tipo = alertaSelecionado.getTipo();
        //String valor = numberFormat.format(alertaSelecionado.getValor());
        String valor = String.valueOf(alertaSelecionado.getValor());
        carregaDadosNaTela(condicao, tipo, valor);

        final Button confirmar = (Button) dialog.findViewById(R.id.btn_confirmar_alerta);

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
                if(alertaSelecionado != null)
                    id_alerta = alertaSelecionado.getID();

                String tipo = getTipoAlertaSelecionado(isCheckedSaldo, isCheckedCredito, isCheckedDespesa);
                int condicao = isCheckedMaior ? 2 : isCheckedMenor ? 1 : 0; // 2 MAIOR e 1 MENOR
                String valor = txtValorAlerta.getText().toString();
                boolean salvo = false;
                try {
                    salvo = alteraAlerta(id_alerta, tipo, condicao, valor);

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
        });*/
        //exibe na tela o dialog
        dialog.show();
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
            Toast.makeText(App.getContext(), "Erro ao alterar o registro.", Toast.LENGTH_SHORT).show();
            return salvo;
        }

        Toast.makeText(App.getContext(), "Alerta alterado com sucesso.", Toast.LENGTH_SHORT).show();
        return salvo;
    }

    @Override
    public void onActionClicked(int position) {
        Toast.makeText(App.getContext(), "Entrou no onActionClicked", Toast.LENGTH_SHORT).show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo; // EX: Alerta de Creditos
        public TextView condicao; // Maior ou Menor
        public TextView valor;   // valor especificado na Condição   EX: Total_de_Despesas  > (maior) 100

        public ViewHolder(View view, final OnAlertasListener onAlertasListener) {
            super(view);
            this.tipo = view.findViewById(R.id.txtTipo);
            this.valor = view.findViewById(R.id.txtValor);
            this.condicao = view.findViewById(R.id.txtCondicao);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        onAlertasListener.onFondoClicked(position);
                    }
                }
            });
        }
    }
}
