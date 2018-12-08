package com.example.edeni.grana.adapter;

import android.app.Activity;
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
import com.example.edeni.grana.CadastroActivity;
import com.example.edeni.grana.MainActivity;
import com.example.edeni.grana.R;
import com.example.edeni.grana.dao.OnAlertasListener;
import com.example.edeni.grana.fragments.AlertasFragment;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.room.AppDatabase;

import java.text.NumberFormat;
import java.util.List;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.ViewHolder> {

    AppDatabase db;
    public List<Alerta> listaDeAlertas;
    private Context context;
    Activity main;
    EditText txtValorAlerta;
    RadioButton radioButtonTipoSaldo;
    RadioButton radioButtonTipoCredito;
    RadioButton radioButtonTipoDespesa;
    RadioButton radioButtonCondicaoMaiorQue;
    RadioButton radioButtonCondicaoMenorQue;

    AlertasAdapter.ViewHolder viewHolder;
    View newView;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Alerta item);
    }

    public AlertasAdapter(Context context, List<Alerta> _listaAlertas,OnItemClickListener listener) {
        this.listaDeAlertas = _listaAlertas;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public AlertasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_alertas, parent, false);
        viewHolder = new AlertasAdapter.ViewHolder(view);
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
        holder.bind(listaDeAlertas.get(position), listener);
        holder.valor.setText(numberFormat.format(listaDeAlertas.get(position).getValor()));

        String tipo = (listaDeAlertas.get(position).getTipo().substring(0, 1).toUpperCase() + (listaDeAlertas.get(position).getTipo().substring(1)));
        holder.tipo.setText(tipo);

        String sinalCondicao = listaDeAlertas.get(position).getCondicao() == 1 ? " < " : " > ";
        holder.condicao.setText(sinalCondicao);
    }

    @Override
    public int getItemCount() {
        if(listaDeAlertas != null)
            return listaDeAlertas.size();
        else
            return 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo; // EX: Alerta de Creditos
        public TextView condicao; // Maior ou Menor
        public TextView valor;   // valor especificado na Condição   EX: Total_de_Despesas  > (maior) 100

        public ViewHolder(View view) {
            super(view);
            this.tipo = view.findViewById(R.id.txtTipo);
            this.valor = view.findViewById(R.id.txtValor);
            this.condicao = view.findViewById(R.id.txtCondicao);
        }
        public void bind(final Alerta item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
