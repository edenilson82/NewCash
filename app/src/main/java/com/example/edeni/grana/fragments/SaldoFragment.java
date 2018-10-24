package com.example.edeni.grana.fragments;

import com.example.edeni.grana.R;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.room.AppDatabase;

import java.text.NumberFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaldoFragment extends Fragment {

    private static String TITULO = "Saldo";

    private AppDatabase db;
    NumberFormat numberFormat;

    TextView txtTotalCredito;
    TextView txtTotalDespesa;
    TextView txtSaldo;
    List<Operacao> operacoes;

    public SaldoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(TITULO);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout for this fragment
        View _view = inflater.inflate(R.layout.fragment_saldo, container, false);

        db = AppDatabase.getInstance(getActivity());

        operacoes = db.operacaoDao().listar();
        numberFormat = NumberFormat.getCurrencyInstance();

        txtTotalCredito = (TextView) _view.findViewById(R.id.txtTotalCredito);
        txtTotalDespesa = (TextView) _view.findViewById(R.id.txtTotalDespesa);
        txtSaldo = (TextView) _view.findViewById(R.id.txtSaldo);

        return _view;
    }

    @Override
    public void onStart() {
        super.onStart();

        calculaOperacoes(operacoes);
    }


    void calculaOperacoes(List<Operacao> operacoes){

        Double somaCredito = 0.0;
        Double somaDespesa = 0.0;
        Double somaTotal = 0.0;

        for (Operacao operacao: operacoes) {

            String tipoOperacao = operacao.getTipo();

        if("Crédito".equals(tipoOperacao) || "crédito".equals(tipoOperacao)){
                somaCredito += operacao.getValor();
            }else{
                somaDespesa += operacao.getValor();
            }

            somaTotal = (somaCredito - somaDespesa);
        }

        txtTotalDespesa.setText(numberFormat.format(somaDespesa.doubleValue()));
        txtTotalCredito.setText(numberFormat.format(somaCredito.doubleValue()));
        txtSaldo.setText(numberFormat.format(somaTotal.doubleValue()));

        if(somaTotal.doubleValue() < 0)
            txtSaldo.setTextColor(Color.RED);
    }

}
