package com.example.edeni.grana.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edeni.grana.R;
import com.example.edeni.grana.model.Bitcoin;
import com.example.edeni.grana.model.Moedas;
import com.example.edeni.grana.room.AppDatabase;
import com.example.edeni.grana.services.ServiceAccessInternet;
import com.example.edeni.grana.services.ServiceCotacao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MoedasFragment extends Fragment {

    private static String TITULO = "Cotação";

    private AppDatabase db;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressBar;
    TextView txt_progressBar;
    Timer timer;

    NumberFormat numberFormat;

    View _view;

    Bitcoin bitcoinRequest;

    // CAMPOS DA TELA
    List<Moedas> listaMoedas;

    TextView txtDolarValorCompra;
    TextView txtDolarValorVenda;
    TextView txtDolarVariacao;
    TextView txtDolarValorMaximo;
    TextView txtDolarValorMinimo;
    TextView txtDolarData;

    TextView txtEuroValorCompra;
    TextView txtEuroValorVenda;
    TextView txtEuroVariacao;
    TextView txtEuroValorMaximo;
    TextView txtEuroValorMinimo;
    TextView txtEuroData;

    TextView txtMaiorPreco;
    TextView txtMenorPreco;
    TextView txtMaiorPrecoCompra;
    TextView txtMenorPrecoVenda;
    TextView txtVolume;
    TextView txtUltimaNegociacao;
    TextView txtDataBitcoin;

    Intent intentServiceCotacao;
    RequestQueue _requestQueue;

    public MoedasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _requestQueue = Volley.newRequestQueue(getContext());
       // intentServiceCotacao = new Intent(getContext(), ServiceCotacao.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(TITULO);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_moedas, container, false);

        mProgressBar = (ProgressBar) _view.findViewById(R.id.progress_bar);
        txt_progressBar = (TextView) _view.findViewById(R.id.txt_progressBar);

        db = AppDatabase.getInstance(getActivity());

        verificaSinalInternet();

        numberFormat = NumberFormat.getCurrencyInstance();

        txtDolarValorCompra = _view.findViewById(R.id.txtDolarValorCompra);
        txtDolarValorVenda = _view.findViewById(R.id.txtDolarValorVenda);
        txtDolarVariacao = _view.findViewById(R.id.txtDolarVariacao);
        txtDolarValorMaximo = _view.findViewById(R.id.txtDolarValorMaximo);
        txtDolarValorMinimo = _view.findViewById(R.id.txtDolarValorMinimo);
        txtDolarData =  _view.findViewById(R.id.txtDataDolar);

        txtEuroValorCompra = _view.findViewById(R.id.txtEuroValorCompra);
        txtEuroValorVenda = _view.findViewById(R.id.txtEuroValorVenda);
        txtEuroVariacao = _view.findViewById(R.id.txtEuroVariacao);
        txtEuroValorMaximo = _view.findViewById(R.id.txtEuroValorMaximo);
        txtEuroValorMinimo = _view.findViewById(R.id.txtEuroValorMinimo);
        txtEuroData =  _view.findViewById(R.id.txtDataEuro);

        txtMaiorPreco = _view.findViewById(R.id.txtMaiorPreco);
        txtMenorPreco = _view.findViewById(R.id.txtMenorPreco);
        txtMaiorPrecoCompra = _view.findViewById(R.id.txtMaiorPrecoCompra);
        txtMenorPrecoVenda = _view.findViewById(R.id.txtMenorPrecoVenda);
        txtVolume = _view.findViewById(R.id.txtVolume);
        txtUltimaNegociacao = _view.findViewById(R.id.txtValorUltimaNegociacao);
        txtDataBitcoin = _view.findViewById(R.id.txtDataBitcoin);

         listaMoedas = new ArrayList<>();

         // AQUI INICIO O SERVIÇO SERVICECOTACAO
        //getActivity().startService(new Intent(getContext(), ServiceCotacao.class));

        mSwipeRefreshLayout = (SwipeRefreshLayout) _view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                verificaSinalInternet();
                ConsultaRequestDolarEuro();
                ConsultaRequestBitcoin();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //getActivity().startService(intentServiceCotacao);

        ConsultaRequestDolarEuro();
        ConsultaRequestBitcoin();

        return _view;
    }

    private void showProgressDialog(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
        txt_progressBar.setVisibility(View.VISIBLE);

    }
    private void hideProgressDialog(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
        txt_progressBar.setVisibility(View.GONE);
    }

    void verificaSinalInternet(){
        // chama o service que verifica se há Conexão com a Internet
        getContext().startService(new Intent(getContext(), ServiceAccessInternet.class));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void ConsultaRequestBitcoin() {

        showProgressDialog(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://www.mercadobitcoin.net/api/BTC/ticker",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        for(int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject= response.getJSONObject("ticker");
                                bitcoinRequest = parseJsonObjectBitcoin(jsonObject);


                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        String data = formateDateUnix(bitcoinRequest.getData());
                        Double maiorPreco = bitcoinRequest.getMaiorPreco();
                        Double menorPreco = bitcoinRequest.getMenorPreco();
                        Double quantidadeNegociada = bitcoinRequest.getQuantidadeNegociada();
                        Double ultimaNegociacao = bitcoinRequest.getUltimaNegociacao();
                        Double maiorPrecoCompra = bitcoinRequest.getMaiorPrecoCompra();
                        Double menorPrecoVenda = bitcoinRequest.getMenorPrecoVenda();

                        DecimalFormat format = new DecimalFormat("0.00");

                        if(bitcoinRequest != null){

                            hideProgressDialog(false);

                            txtMaiorPreco.setText(numberFormat.format(maiorPreco.doubleValue()));
                            txtMenorPreco.setText(numberFormat.format(menorPreco.doubleValue()));
                            txtVolume.setText(format.format(quantidadeNegociada)+" BTC");
                            txtUltimaNegociacao.setText(numberFormat.format(ultimaNegociacao.doubleValue()));
                            txtMaiorPrecoCompra.setText(numberFormat.format(maiorPrecoCompra.doubleValue()));
                            txtMenorPrecoVenda.setText(numberFormat.format(menorPrecoVenda.doubleValue()));
                            txtDataBitcoin.setText(data);
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgressDialog(false);
                        /*
                        loadCotacao = false;
                        Snackbar.make(
                                _view,
                                "Não foi possível carregar as cotações. Verifique a internet.",
                                Snackbar.LENGTH_LONG
                        ).show();
                        */
                    }
                });

        //RequestQueue _requestQueue = Volley.newRequestQueue(getContext());
        int socketTimeout = 3000;
        RetryPolicy policy  = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        _requestQueue.add(request);
    }

    public void ConsultaRequestDolarEuro() {

        String URL = "https://economia.awesomeapi.com.br/all";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                for(int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObjectDolar = response.getJSONObject("USD");
                        JSONObject jsonObjectEuro = response.getJSONObject("EUR");

                        parseJsonObjectDolar(jsonObjectDolar);
                        parseJsonObjectEuro(jsonObjectEuro);
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (Moedas moeda:listaMoedas) {

                    String nomeMoeda = moeda.getMoeda();

                    if(nomeMoeda.equals("USD")){

                        String data = moeda.getData();
                        txtDolarValorCompra.setText(numberFormat.format(moeda.getValorCompra().doubleValue()));
                        txtDolarValorVenda.setText(numberFormat.format(moeda.getValorVenda().doubleValue()));
                        txtDolarVariacao.setText(numberFormat.format(moeda.getVariacao().doubleValue()));
                        txtDolarValorMaximo.setText(numberFormat.format(moeda.getValorMaximo().doubleValue()));
                        txtDolarValorMinimo.setText(numberFormat.format(moeda.getValorMinimo().doubleValue()));
                        txtDolarData.setText(formateDate(data));

                    }else if(nomeMoeda.equals("EUR")){

                        String data = moeda.getData();
                        txtEuroValorCompra.setText(numberFormat.format(moeda.getValorCompra().doubleValue()));
                        txtEuroValorVenda.setText(numberFormat.format(moeda.getValorVenda().doubleValue()));
                        txtEuroVariacao.setText(numberFormat.format(moeda.getVariacao().doubleValue()));
                        txtEuroValorMaximo.setText(numberFormat.format(moeda.getValorMaximo().doubleValue()));
                        txtEuroValorMinimo.setText(numberFormat.format(moeda.getValorMinimo().doubleValue()));
                        txtEuroData.setText(formateDate(data));

                    }
                }

            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        /*
                        loadCotacao = false;
                        Snackbar.make(
                                _view,
                                "Não foi possível carregar as cotações. Verifique a internet.",
                                Snackbar.LENGTH_LONG
                        ).show();
                        */
                    }
                });

        RequestQueue _requestQueue = Volley.newRequestQueue(getContext());
        _requestQueue.add(request);
    }

    public String formateDateUnix(String data){
        long dv = Long.valueOf(data)*1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        return new SimpleDateFormat("dd/MM/yyyy hh:mma").format(df);
    }

    private String formateDate(String data){

        String[] dateSplit = data.split("-");
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2].substring(0,2));

        return day + "/" + month + "/" + year;
    }

    private void parseJsonObjectDolar(JSONObject jsonObjectDolar){
        try{

            String nomeMoeda = jsonObjectDolar.getString("code");  // nome da moeda
            String valorCompra = jsonObjectDolar.getString("bid");  //  valor compra
            String valorVenda = jsonObjectDolar.getString("ask");  //  valor de venda
            String variacao = jsonObjectDolar.getString("varBid");  //  variação
            String valorMaximo = jsonObjectDolar.getString("high");  //  valor maximo
            String valorMinimo = jsonObjectDolar.getString("low");  // valor minimo
            String data = jsonObjectDolar.getString("create_date"); //data da cotação

            Moedas dolar = new Moedas();
            dolar.setMoeda(nomeMoeda);
            dolar.setValorCompra(Double.parseDouble(valorCompra));
            dolar.setValorVenda(Double.parseDouble(valorVenda));
            dolar.setValorMaximo(Double.parseDouble(valorMaximo));
            dolar.setValorMinimo(Double.parseDouble(valorMinimo));
            dolar.setVariacao(Double.parseDouble(variacao));
            dolar.setData(data);

            listaMoedas.add(dolar);
        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonObjectEuro(JSONObject jsonObjectEuro){
        try{
            String nomeMoeda = jsonObjectEuro.getString("code");  // nome da moeda
            String valorCompra = jsonObjectEuro.getString("bid");  //  valor compra
            String valorVenda = jsonObjectEuro.getString("ask");  //  valor de venda
            String variacao = jsonObjectEuro.getString("varBid");  //  variação
            String valorMaximo = jsonObjectEuro.getString("high");  //  valor maximo
            String valorMinimo = jsonObjectEuro.getString("low");  // valor minimo
            String data = jsonObjectEuro.getString("create_date"); //data da cotação

            Moedas euro = new Moedas();
            euro.setMoeda(nomeMoeda);
            euro.setValorCompra(Double.parseDouble(valorCompra));
            euro.setValorVenda(Double.parseDouble(valorVenda));
            euro.setValorMaximo(Double.parseDouble(valorMaximo));
            euro.setValorMinimo(Double.parseDouble(valorMinimo));
            euro.setVariacao(Double.parseDouble(variacao));
            euro.setData(data);

            listaMoedas.add(euro);
        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitcoin parseJsonObjectBitcoin(JSONObject jsonObjectBitcoin){
        try{
            String maiorPreco = jsonObjectBitcoin.getString("high");
            String menorPreco = jsonObjectBitcoin.getString("low");
            String quantidadeNegociada = jsonObjectBitcoin.getString("vol");
            String ultimaNegociacao = jsonObjectBitcoin.getString("last");
            String maiorPrecoCompra = jsonObjectBitcoin.getString("buy");
            String menorPrecoVenda = jsonObjectBitcoin.getString("sell");
            String data = jsonObjectBitcoin.getString("date");

            Bitcoin bitcoin = new Bitcoin();
            bitcoin.setMaiorPreco(Double.parseDouble(maiorPreco));
            bitcoin.setMenorPreco(Double.parseDouble(menorPreco));
            bitcoin.setQuantidadeNegociada(Double.parseDouble(quantidadeNegociada));
            bitcoin.setUltimaNegociacao(Double.parseDouble(ultimaNegociacao));
            bitcoin.setMaiorPrecoCompra(Double.parseDouble(maiorPrecoCompra));
            bitcoin.setMenorPrecoVenda(Double.parseDouble(menorPrecoVenda));
            bitcoin.setData(data);

           return  bitcoin;

        }catch(JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }
}
