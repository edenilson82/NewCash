package com.example.edeni.grana.adapter;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.StringPrepParseException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.edeni.grana.CadastroActivity;
import com.example.edeni.grana.MainActivity;
import com.example.edeni.grana.R;
import com.example.edeni.grana.dao.OnOperacoesListener;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.room.AppDatabase;

import java.text.NumberFormat;
import java.util.List;

public class OperacaoAdapter extends RecyclerView.Adapter<OperacaoAdapter.ViewHolder> implements OnOperacoesListener {

    AppDatabase db;
    private List<Operacao> listaOperacao;
    private Context context;
    private ListaOperacaoOnClickListener listaOperacaoOnClickListener;

    public OperacaoAdapter(Context context, List<Operacao> _listaOperacao, ListaOperacaoOnClickListener listaOperacaoOnClickListener) {
        this.listaOperacao = _listaOperacao;
        this.context = context;
        this.listaOperacaoOnClickListener = listaOperacaoOnClickListener;

    }

    public OperacaoAdapter(Context context, List<Operacao> _listaOperacao) {
        this.listaOperacao = _listaOperacao;
        this.context = context;
    }

    public void setListaOperacao(List<Operacao> listaCash) {
        this.listaOperacao = listaCash;
    }

    // nesse metodo eu só altero o estilo do tipo da operação
    private void SetStyleViewHolder(OperacaoAdapter.ViewHolder holder){

        if(holder.tipo.getText().equals("Crédito")){
            holder.tipo.setTextColor(Color.BLUE);
        }else{
            holder.tipo.setTextColor(Color.RED);
        }
    }

    @Override
    public OperacaoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_cash, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final OperacaoAdapter.ViewHolder holder, final int position) {

        db = AppDatabase.getInstance(context);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        holder.valor.setText(numberFormat.format(listaOperacao.get(position).getValor()));
        holder.descricao.setText(listaOperacao.get(position).getDescricao());
        holder.data.setText(listaOperacao.get(position).getData());
        holder.tipo.setText(listaOperacao.get(position).getTipo());

        long id = listaOperacao.get(position).getCategoria_Id();
        Categoria categoria = db.categoriaDao().procurarPorId(id);
        holder.categoria.setText(categoria.getNomeCategoria());

        // Crio o estilo da Lista - Cores, tamanho
        SetStyleViewHolder(holder);

    }

    @Override
    public int getItemCount() {
        return listaOperacao.size();
    }

    @Override
    public void onFondoClicked(int position) {
        Intent intent = new Intent(context, CadastroActivity.class);
        intent.putExtra("operacao", listaOperacao.get(position));
        context.startActivity(intent);
    }

    @Override
    public void onActionClicked(int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView descricao;
        public TextView data;
        public TextView tipo;
        public TextView valor;
        public TextView categoria;

        public ViewHolder(View view, final OnOperacoesListener onOperacoesListener) {
            super(view);
            this.descricao = view.findViewById(R.id.txtDescricao);
            this.data = view.findViewById(R.id.txtData);
            this.tipo = view.findViewById(R.id.txtTipo);
            this.valor = view.findViewById(R.id.txtValor);
            this.categoria = view.findViewById(R.id.txtCategoria);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        onOperacoesListener.onFondoClicked(position);
                    }
                }
            });
        }
    }

    public interface ListaOperacaoOnClickListener {
        public void onClickCash(View view, int pos);
    }
}
